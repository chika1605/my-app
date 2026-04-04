package kg.rubicon.my_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoiceRequest {

    private String text;
    private String language;

}
