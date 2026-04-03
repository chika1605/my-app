package kg.rubicon.my_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "person_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"person_id", "language"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private short language;

    private String fullName;
    private String normalizedName;
    private String birthPlace;
    private String deathPlace;
    private String region;
    private String district;
    private String occupation;

    @Column(columnDefinition = "TEXT")
    private String charge;

    @Column(columnDefinition = "TEXT")
    private String sentence;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public Language getLanguageAsEnum() {
        return Language.getFromId(this.language);
    }

}
