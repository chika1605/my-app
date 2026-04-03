package kg.rubicon.my_app.ml;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ml.api")
@Component
@Data
public class MLProperties {

    private String url;

    private Routers routers;

    @Data
    public static class Routers {
        private String saveDoc;
        private String getInfo;
        private String chat;
        private String asr;
    }
}
