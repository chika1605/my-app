package kg.rubicon.my_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "person_translations")
public class PersonTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;

    private String fullName;
    private String birthPlace;
    private String deathPlace;
    private String region;
    private String district;
    private String occupation;

    @Column(columnDefinition = "TEXT")
    private String charge;

    private String chargeCategory;

    @Column(columnDefinition = "TEXT")
    private String sentence;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

}
