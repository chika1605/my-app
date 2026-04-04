package kg.rubicon.my_app.ml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class MlErrorResponse {
    private Error error;

    @Data
    public static class Error {
        private String code;
        private String message;
        private Details details;
    }

    @Data
    public static class Details {
        @JsonProperty("person_id")
        private Long personId;

        @JsonProperty("full_name")
        private String fullName;

        @JsonProperty("normalized_name")
        private String normalizedName;

        @JsonProperty("birth_year")
        private Integer birthYear;

        private Double confidence;

        @JsonProperty("matched_fields")
        private List<String> matchedFields;
    }
}
