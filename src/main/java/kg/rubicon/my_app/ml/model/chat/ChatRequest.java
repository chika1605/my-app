package kg.rubicon.my_app.ml.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatRequest {
    private String question;
    private List<ChatMessageHistory> history;
}
