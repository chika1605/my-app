package kg.rubicon.my_app.ml;

import kg.rubicon.my_app.chat.dto.AsrResponse;
import kg.rubicon.my_app.ml.model.SaveDocRequest;
import kg.rubicon.my_app.ml.model.SaveDocResponse;
import kg.rubicon.my_app.ml.dto.GetInfoResponse;
import kg.rubicon.my_app.ml.model.chat.ChatRequest;
import kg.rubicon.my_app.ml.model.chat.ChatResponse;
import kg.rubicon.my_app.util.MultipartInputStreamFileResource;
import kg.rubicon.my_app.util.exception.MLIntegrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
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

    public GetInfoResponse getInfo(String text) {
        return client.post(
                properties.getRouters().getGetInfo(),
                Map.of("text", text),
                GetInfoResponse.class
        );
    }

    public ChatResponse chat(ChatRequest request) {
        return client.post(
                properties.getRouters().getChat(),
                request,
                ChatResponse.class
        );
    }

    public AsrResponse recognizeSpeech(MultipartFile file) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(
                    file.getInputStream(),
                    file.getOriginalFilename()
            ));

            return client.postMultipart(
                    properties.getRouters().getAsr(),
                    body,
                    AsrResponse.class
            );
        } catch (IOException e) {
            throw new MLIntegrationException(
                    "Failed to read audio file: " + e.getMessage(),
                    "ML_ASR_ERROR",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

}
