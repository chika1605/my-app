package kg.rubicon.my_app.ml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PersonCard(
        @JsonProperty("full_name")
        String fullName,

        @JsonProperty("normalized_name")
        String normalizedName,

        @JsonProperty("birth_year")
        Integer birthYear,

        @JsonProperty("death_year")
        Integer deathYear,

        @JsonProperty("birth_date")
        String birthDate,

        @JsonProperty("death_date")
        String deathDate,

        @JsonProperty("birth_place")
        String birthPlace,

        @JsonProperty("death_place")
        String deathPlace,

        String region,
        String district,
        String occupation,
        String charge,

        @JsonProperty("arrest_date")
        String arrestDate,

        String sentence,

        @JsonProperty("sentence_date")
        String sentenceDate,

        @JsonProperty("rehabilitation_date")
        String rehabilitationDate,

        String biography
) {}
