package kg.rubicon.my_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PersonCreationRequest {

    private Integer birthYear;
    private Integer deathYear;

    private LocalDate birthDate;
    private LocalDate deathDate;
    private String normalizedName;

    private LocalDate arrestDate;
    private LocalDate sentenceDate;
    private LocalDate rehabilitationDate;

    @NotNull(message = "documentId is required")
    private Long documentId;

    @Valid
    private List<TranslationRequest> translations;

    @Getter
    @Setter
    public static class TranslationRequest {

        private short language;

        @NotBlank(message = "FullName is required")
        private String fullName;

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
