package kg.rubicon.my_app.ml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record GetInfoResponse(
        String type,
        Map<String, Object> result,
        @JsonProperty("normalized_names") List<String> normalizedNames,
        @JsonProperty("missing_fields") List<String> missingFields,
        List<String> warnings
) {}
