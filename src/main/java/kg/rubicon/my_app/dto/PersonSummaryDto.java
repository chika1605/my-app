package kg.rubicon.my_app.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class PersonSummaryDto {
    private Long id;
    private String imageName;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private Integer birthYear;
    private Integer deathYear;
    private short status;
    private Map<String, TranslationSummary> translations;

    @Data
    public static class TranslationSummary {
        private String fullName;
        private String birthPlace;
        private String occupation;
        private String charge;
    }
}
