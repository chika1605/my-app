package kg.rubicon.my_app.ml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SingleResult(
        PersonCard ky,
        PersonCard ru,
        PersonCard en,
        PersonCard tr,

        @JsonProperty("normalized_name")
        String normalizedName
) {}
