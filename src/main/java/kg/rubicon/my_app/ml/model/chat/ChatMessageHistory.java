package kg.rubicon.my_app.ml.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageHistory {
    private String question;
    private String answer;
}
