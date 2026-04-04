package kg.rubicon.my_app.ml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record GetInfoResponse(
        String type,
        @JsonProperty("normalized_names") List<String> normalizedNames,
        @JsonProperty("result") SingleResult  result,
        List<String> warnings
) {}
