package kg.rubicon.my_app.ml;

import kg.rubicon.my_app.util.exception.MLIntegrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class MLClient {

    private final MLProperties properties;
    private final RestTemplate restTemplate;

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public <T> T post(String path, Object requestBody, Class<T> responseType) {
        String url = properties.getUrl() + path;
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, buildHeaders());

        log.debug("ML POST {} | body: {}", url, requestBody);

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            log.debug("ML response status: {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("ML client error: {}", e.getResponseBodyAsString());
            throw new MLIntegrationException("ML client error: " + e.getResponseBodyAsString(), "ML_CLIENT_ERROR", HttpStatus.BAD_REQUEST);
        } catch (HttpServerErrorException e) {
            log.error("ML server error: {}", e.getResponseBodyAsString());
            throw new MLIntegrationException("ML server error: " + e.getResponseBodyAsString(), "ML_SERVER_ERROR", HttpStatus.BAD_GATEWAY);
        }
    }
}
