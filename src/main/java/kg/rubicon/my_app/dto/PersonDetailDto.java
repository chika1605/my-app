package kg.rubicon.my_app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class PersonDetailDto {
    private Long id;
    private String imageName;
    private Integer birthYear;
    private Integer deathYear;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private LocalDate arrestDate;
    private LocalDate sentenceDate;
    private LocalDate rehabilitationDate;
    private short status;

    private Map<String, TranslationDetail> translations;

    // только имена связанных документов
    private List<DocumentRef> documents;

    @Data
    public static class TranslationDetail {
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

    @Data
    public static class DocumentRef {
        private Long id;
        private String originalName;
    }
}
