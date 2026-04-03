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

    private Integer repressionYear;

    private String photoUrl;

    private LocalDate arrestDate;
    private LocalDate sentenceDate;
    private LocalDate rehabilitationDate;

    @Builder.Default
    @Column(nullable = false)
    private short status = PersonStatus.PENDING.getId();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PersonTranslation> translations;

    @ManyToMany
    @JoinTable(
            name = "person_documents",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<Document> documentsManyToMany;

    public PersonStatus getStatusAsEnum() {
        return PersonStatus.getFromId(this.status);
    }

}