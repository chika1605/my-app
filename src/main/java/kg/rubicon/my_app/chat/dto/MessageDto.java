package kg.rubicon.my_app.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageDto {
    private Long id;
    private String question;
    private String answer;
    private List<SourceDto> sources;
    private LocalDateTime createdAt;
}