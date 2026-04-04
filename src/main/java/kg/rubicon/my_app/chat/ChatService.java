package kg.rubicon.my_app.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.rubicon.my_app.chat.dto.*;
import kg.rubicon.my_app.ml.MlService;
import kg.rubicon.my_app.ml.model.chat.ChatMessageHistory;
import kg.rubicon.my_app.ml.model.chat.ChatRequest;
import kg.rubicon.my_app.ml.model.chat.ChatResponse;
import kg.rubicon.my_app.model.Chat;
import kg.rubicon.my_app.model.Message;
import kg.rubicon.my_app.repository.ChatRepository;
import kg.rubicon.my_app.repository.MessageRepository;
import kg.rubicon.my_app.repository.UserRepository;
import kg.rubicon.my_app.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final MlService mlService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     *  Получить все чаты пользователя (без сообщений)
     */
    public List<ChatDto> getUserChats(Long userId) {
        return chatRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toChatDto)
                .toList();
    }

    /**
     * Получить один чат с полной историей
     */
    public ChatWithMessagesDto getChatWithMessages(Long chatId, Long userId) {
        Chat chat = chatRepository.findByIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with ID: %d not found".formatted(chatId)));
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
        return toChatWithMessagesDto(chat, messages);
    }

    /**
     * Отправить сообщение (создаёт новый чат если chatId == null)
     */
    @Transactional
    public MessageDto sendMessage(Long userId, Long chatId, SendMessageRequest request) {

        Chat chat;
        List<Message> history;

        if (chatId == null) {
            // TODO: реализовать умная создания заголовка
            chat = new Chat();
            chat.setUser(userRepository.getReferenceById(userId));
            chat.setTitle(truncate(request.getQuestion(), 30));
            chatRepository.save(chat);
            history = List.of();
        } else {
            chat = chatRepository.findByIdAndUserId(chatId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Chat with ID: %d not found".formatted(chatId)));
            history = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
        }

        List<ChatMessageHistory> mlHistory = history.stream()
                .map(m -> new ChatMessageHistory(m.getQuestion(), m.getAnswer()))
                .toList();

        ChatResponse mlResponse = mlService.chat(
                new ChatRequest(request.getQuestion(), mlHistory)
        );

        Message message = new Message();
        message.setChat(chat);
        message.setQuestion(request.getQuestion());
        message.setAnswer(mlResponse.getAnswer());
        message.setSourcesJson(serializeSources(mlResponse));
        messageRepository.save(message);

        return toMessageDto(message, mlResponse);
    }

    /**
     * Удалить чат
     */
    @Transactional
    public void deleteChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findByIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with ID: %d not found".formatted(chatId)));
        chatRepository.delete(chat);
    }

    private ChatDto toChatDto(Chat chat) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setTitle(chat.getTitle());
        dto.setCreatedAt(chat.getCreatedAt());
        return dto;
    }

    private ChatWithMessagesDto toChatWithMessagesDto(Chat chat, List<Message> messages) {
        ChatWithMessagesDto dto = new ChatWithMessagesDto();
        dto.setId(chat.getId());
        dto.setTitle(chat.getTitle());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setMessages(messages.stream().map(m -> toMessageDto(m, null)).toList());
        return dto;
    }

    private MessageDto toMessageDto(Message message, ChatResponse mlResponse) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setQuestion(message.getQuestion());
        dto.setAnswer(message.getAnswer());
        dto.setCreatedAt(message.getCreatedAt());

        if (mlResponse != null && mlResponse.getSources() != null) {
            dto.setSources(mlResponse.getSources().stream()
                    .map(s -> {
                        SourceDto src = new SourceDto();
                        src.setDocumentId(s.getDocumentId());
                        return src;
                    }).toList());
        } else {
            dto.setSources(deserializeSources(message.getSourcesJson()));
        }

        return dto;
    }

    private String serializeSources(ChatResponse mlResponse) {
        try {
            return objectMapper.writeValueAsString(mlResponse.getSources());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<SourceDto> deserializeSources(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private String truncate(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

    public AsrResponse recognizeSpeech(MultipartFile file) {
        return mlService.recognizeSpeech(file);
    }
}
