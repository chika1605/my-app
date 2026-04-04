package kg.rubicon.my_app.dto;

import kg.rubicon.my_app.ml.dto.SingleResult;

public record UploadResult(
        String type,
        Long documentId,
        SingleResult result,
        String filename
) {}
