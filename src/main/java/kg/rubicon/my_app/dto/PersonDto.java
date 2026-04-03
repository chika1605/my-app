package kg.rubicon.my_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PersonDto {

    private Integer birthYear;
    private Integer deathYear;

    private LocalDate birthDate;
    private LocalDate deathDate;

    private LocalDate arrestDate;
    private LocalDate sentenceDate;
    private LocalDate rehabilitationDate;

    private List<TranslationDto> translations;

    @Getter
    @Setter
    public static class TranslationDto {

        private short language;

        private String fullName;

        private String normalizedName;
        private String birthPlace;
        private String deathPlace;
        private String region;
        private String district;
        private String occupation;
        private String charge;
        private String sentence;
        private String biography;
    }
}
