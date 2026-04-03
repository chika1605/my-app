package kg.rubicon.my_app.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
