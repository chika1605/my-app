package kg.rubicon.my_app.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonDataDto(
        Integer birthYear,
        Integer deathYear,
        Integer repressionYear,
        LocalDate birthDate,
        LocalDate deathDate,
        LocalDate arrestDate,
        LocalDate sentenceDate,
        LocalDate rehabilitationDate,
        Map<String, TranslationDto> translations
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TranslationDto(
            String fullName,
            String normalizedName,
            String birthPlace,
            String deathPlace,
            String region,
            String district,
            String occupation,
            String charge,
            String sentence,
            String biography
    ) {}
}
