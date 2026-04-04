package kg.rubicon.my_app.ml;

import kg.rubicon.my_app.ml.model.SaveDocRequest;
import kg.rubicon.my_app.ml.model.SaveDocResponse;
import kg.rubicon.my_app.ml.dto.ExtractPdfTextResponse;
import kg.rubicon.my_app.ml.dto.GetInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public ExtractPdfTextResponse extractPdfText(byte[] pdfBytes, String filename) {
        return client.postMultipart(
                properties.getRouters().getExtractPdfText(),
                "file",
                pdfBytes,
                filename,
                ExtractPdfTextResponse.class
        );
    }

}
