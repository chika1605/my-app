package kg.rubicon.my_app.repository;

import kg.rubicon.my_app.model.PersonTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonTranslationRepository extends JpaRepository<PersonTranslation, Long> {

    Optional<PersonTranslation> findByPersonIdAndLanguage(Long personId, short language);
}