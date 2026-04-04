package kg.rubicon.my_app.ml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExtractPdfTextResponse(
        String text,
        List<String> warnings,
        @JsonProperty("extraction_mode") String extractionMode
) {}