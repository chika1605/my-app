package kg.rubicon.my_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonCreationRequest {

    private Integer birthYear;
    private Integer deathYear;

    private LocalDate birthDate;
    private LocalDate deathDate;

    private LocalDate arrestDate;
    private LocalDate sentenceDate;
    private LocalDate rehabilitationDate;

    private Long documentId;

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
