package kg.rubicon.my_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "persons")
@EntityListeners(AuditingEntityListener.class)
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

    @Column(nullable = false)
    private short status = PersonStatus.PENDING.getId();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonTranslation> translations;

    public PersonStatus getStatusAsEnum() {
        return PersonStatus.getFromId(this.status);
    }

}