package kg.rubicon.my_app.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PersonFilterRequest {
    // фильтры по Person
    private Integer birthYear;
    private Integer deathYear;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private LocalDate arrestDate;
    private LocalDate sentenceDate;
    private LocalDate rehabilitationDate;

    // фильтры по переводу (фронт даёт на русском)
    private String name;       // поиск по fullName
    private String region;
    private String district;
    private String occupation;

    // пагинация
    private int page = 0;
    private int size = 20;
}
