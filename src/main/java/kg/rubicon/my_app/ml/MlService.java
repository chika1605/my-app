package kg.rubicon.my_app.ml;

import kg.rubicon.my_app.ml.dto.GetInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MlService {

    private final MLClient client;
    private final MLProperties properties;

    public GetInfoResponse getInfo(String text) {
        return client.post(
                properties.getRouters().getGetInfo(),
                Map.of("text", text),
                GetInfoResponse.class
        );
    }
}
