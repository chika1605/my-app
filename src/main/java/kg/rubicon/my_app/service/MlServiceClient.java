package kg.rubicon.my_app.service;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MlServiceClient {

    private final RestClient restClient;

    public MlServiceClient(@Value("${ml.service.url}") String mlServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(mlServiceUrl)
                .build();
    }

    public void saveDoc(SaveDocRequest request) {
        restClient.post()
                .uri("/ai/save_doc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public record SaveDocRequest(
            Long person_id,
            Long document_id,
            String filename,
            String text,
            String language_code
    ) {}
}
