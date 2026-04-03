package kg.rubicon.my_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer birthYear;
    private Integer deathYear;

    private LocalDate birthDate;
    private LocalDate deathDate;

    private LocalDate arrestDate;
    private LocalDate sentenceDate;
    private LocalDate rehabilitationDate;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonTranslation> translations;

}