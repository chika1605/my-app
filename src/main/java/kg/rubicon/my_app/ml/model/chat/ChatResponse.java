package kg.rubicon.my_app.ml.model.chat;

import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private String answer;
    private List<ChatSource> sources;
}