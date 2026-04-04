package kg.rubicon.my_app.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank
    private String question;
}
