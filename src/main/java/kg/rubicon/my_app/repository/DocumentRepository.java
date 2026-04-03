package kg.rubicon.my_app.repository;



import kg.rubicon.my_app.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {}