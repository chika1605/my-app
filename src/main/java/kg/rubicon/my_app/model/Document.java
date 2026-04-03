package kg.rubicon.my_app.model;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;   // delo_baytemirova.txt
    private String fileName;       // 3f2a1b...-uuid.txt  (на диске)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String fullText;

    private LocalDateTime uploadedAt;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;
}
