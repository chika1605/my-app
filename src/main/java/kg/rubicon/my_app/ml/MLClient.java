package kg.rubicon.my_app.ml;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.rubicon.my_app.ml.dto.MlErrorResponse;
import kg.rubicon.my_app.util.exception.DuplicatePersonException;
import kg.rubicon.my_app.util.exception.MLIntegrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );
            log.debug("ML response status: {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException.Conflict e) {
            throw handleConflict(e);
        } catch (HttpClientErrorException e) {
            throw handleClientError(e);
        } catch (HttpServerErrorException e) {
            throw handleServerError(e);
        }
    }

    public <T> T postMultipart(String path, String fieldName, byte[] fileBytes, String filename, Class<T> responseType) {
        String url = properties.getUrl() + path;

        ByteArrayResource resource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() { return filename; }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(fieldName, resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        log.debug("ML POST multipart {} | file: {}", url, filename);

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException.Conflict e) {
            throw handleConflict(e);
        } catch (HttpClientErrorException e) {
            throw handleClientError(e);
        } catch (HttpServerErrorException e) {
            throw handleServerError(e);
        }
    }

    public <T> T get(String path, Class<T> responseType, Object... uriVars) {
        String url = properties.getUrl() + path;
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        log.debug("ML GET {}", url);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    responseType,
                    uriVars
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw handleClientError(e);
        } catch (HttpServerErrorException e) {
            throw handleServerError(e);
        }
    }

    private MLIntegrationException handleClientError(HttpClientErrorException e) {
        log.error("ML client error: {}", e.getResponseBodyAsString());
        return new MLIntegrationException(
                "ML client error: " + e.getResponseBodyAsString(),
                "ML_CLIENT_ERROR",
                HttpStatus.BAD_REQUEST
        );
    }

    public <T> T postMultipart(String path, MultiValueMap<String, Object> body, Class<T> responseType) {
        String url = properties.getUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        log.debug("ML POST multipart {}", url);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw handleClientError(e);
        } catch (HttpServerErrorException e) {
            throw handleServerError(e);
        }
    }

    private MLIntegrationException handleServerError(HttpServerErrorException e) {
        log.error("ML server error: {}", e.getResponseBodyAsString());
        return new MLIntegrationException(
                "ML server error: " + e.getResponseBodyAsString(),
                "ML_SERVER_ERROR",
                HttpStatus.BAD_GATEWAY
        );
    }

    private DuplicatePersonException handleConflict(HttpClientErrorException.Conflict e) {
        try {
            MlErrorResponse errorResponse = new ObjectMapper()
                    .findAndRegisterModules()
                    .readValue(e.getResponseBodyAsString(), MlErrorResponse.class);
            log.warn("Duplicate person detected: {}", errorResponse.getError().getDetails());
            return new DuplicatePersonException(errorResponse.getError().getDetails());
        } catch (Exception parseEx) {
            log.error("Failed to parse 409 response: {}", e.getResponseBodyAsString());
            return new DuplicatePersonException(null);
        }
    }

}