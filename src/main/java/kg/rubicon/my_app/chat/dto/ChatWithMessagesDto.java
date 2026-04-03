package kg.rubicon.my_app.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatWithMessagesDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private List<MessageDto> messages;
}