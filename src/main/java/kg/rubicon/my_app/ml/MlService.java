package kg.rubicon.my_app.ml;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MlService {

    private final MLClient client;
    private final MLProperties properties;

}
