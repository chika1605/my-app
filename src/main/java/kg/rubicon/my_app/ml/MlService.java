package kg.rubicon.my_app.ml;

import kg.rubicon.my_app.ml.model.SaveDocRequest;
import kg.rubicon.my_app.ml.model.SaveDocResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MlService {

    private final MLClient client;
    private final MLProperties properties;

    public SaveDocResponse saveDoc(SaveDocRequest request) {
        return client.post(
                properties.getRouters().getSaveDoc(),
                request,
                SaveDocResponse.class
        );
    }

}
