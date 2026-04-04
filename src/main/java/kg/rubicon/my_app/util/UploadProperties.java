package kg.rubicon.my_app.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "upload")
@Component
@Data
public class UploadProperties {

    private String dir;
    private Map<String, String> folders;

}
