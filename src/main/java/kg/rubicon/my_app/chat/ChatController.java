package kg.rubicon.my_app.chat;

import jakarta.validation.Valid;
import kg.rubicon.my_app.chat.dto.*;
import kg.rubicon.my_app.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SecurityService securityService;

    /**
     * Получить все чаты текущего пользователя
     */
    @GetMapping
    public List<ChatDto> getChats() {
        return chatService.getUserChats(securityService.getCurrentAuthUser());
    }

    /**
     * Получить чат с историей
     */
    @GetMapping("/{chatId}")
    public ChatWithMessagesDto getChat(@PathVariable Long chatId) {
        return chatService.getChatWithMessages(chatId, securityService.getCurrentAuthUser());
    }

    /**
     * Новый чат + первое сообщение
     */
    @PostMapping
    public MessageDto startChat(@Valid @RequestBody SendMessageRequest request) {
        return chatService.sendMessage(securityService.getCurrentAuthUser(), null, request);
    }

    /**
     * Продолжить существующий чат
     */
    @PostMapping("/{chatId}/messages")
    public MessageDto sendMessage(@PathVariable Long chatId,
                                  @Valid @RequestBody SendMessageRequest request) {
        return chatService.sendMessage(securityService.getCurrentAuthUser(), chatId, request);
    }

    /**
     * Удалить чат
     */
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId, securityService.getCurrentAuthUser());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/asr")
    public AsrTextResponse recognizeSpeech(@RequestParam("file") MultipartFile file) {
        AsrResponse response = chatService.recognizeSpeech(file);
        return new AsrTextResponse(response.getText());
    }
}
