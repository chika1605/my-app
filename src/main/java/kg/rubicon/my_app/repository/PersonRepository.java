package kg.rubicon.my_app.repository;

import kg.rubicon.my_app.dto.PersonSummaryProjection;
import kg.rubicon.my_app.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Modifying
    @Query(value = "UPDATE persons SET status = :status WHERE id = :id", nativeQuery = true)
    void personUpdateStatusById(@Param("id") Long id, @Param("status") short status);

    @Query(value = """
    SELECT DISTINCT p.* FROM persons p
    WHERE EXISTS (
        SELECT 1 FROM unnest(string_to_array(:names, ',')) AS input_name
        WHERE input_name LIKE CONCAT(p.normalized_name, '%')
        OR p.normalized_name LIKE CONCAT(input_name, '%')
    )
    """, nativeQuery = true)
    List<Person> findByNormalizedNamesBidirectional(@Param("names") String names);

    @Query(value = """
    SELECT
        p.id                                                    AS id,
        p.image_name                                            AS imageName,
        p.birth_date                                            AS birthDate,
        p.death_date                                            AS deathDate,
        p.birth_year                                            AS birthYear,
        p.death_year                                            AS deathYear,
        p.status                                                AS status,
        MAX(CASE WHEN t.language = :ruLang THEN t.full_name     END) AS ruFullName,
        MAX(CASE WHEN t.language = :ruLang THEN t.birth_place   END) AS ruBirthPlace,
        MAX(CASE WHEN t.language = :ruLang THEN t.occupation    END) AS ruOccupation,
        MAX(CASE WHEN t.language = :ruLang THEN t.charge        END) AS ruCharge,
        MAX(CASE WHEN t.language = :kyLang THEN t.full_name     END) AS kyFullName,
        MAX(CASE WHEN t.language = :kyLang THEN t.birth_place   END) AS kyBirthPlace,
        MAX(CASE WHEN t.language = :kyLang THEN t.occupation    END) AS kyOccupation,
        MAX(CASE WHEN t.language = :kyLang THEN t.charge        END) AS kyCharge,
        MAX(CASE WHEN t.language = :enLang THEN t.full_name     END) AS enFullName,
        MAX(CASE WHEN t.language = :enLang THEN t.birth_place   END) AS enBirthPlace,
        MAX(CASE WHEN t.language = :enLang THEN t.occupation    END) AS enOccupation,
        MAX(CASE WHEN t.language = :enLang THEN t.charge        END) AS enCharge,
        MAX(CASE WHEN t.language = :trLang THEN t.full_name     END) AS trFullName,
        MAX(CASE WHEN t.language = :trLang THEN t.birth_place   END) AS trBirthPlace,
        MAX(CASE WHEN t.language = :trLang THEN t.occupation    END) AS trOccupation,
        MAX(CASE WHEN t.language = :trLang THEN t.charge        END) AS trCharge
    FROM persons p
    LEFT JOIN person_translations t ON t.person_id = p.id
    LEFT JOIN person_translations t_ru ON t_ru.person_id = p.id AND t_ru.language = :ruLang
    WHERE (
        :name IS NULL
        OR LOWER(t_ru.full_name) LIKE LOWER(CONCAT('%', :name, '%'))
        OR (:namePart1 IS NULL OR LOWER(t_ru.full_name) LIKE LOWER(CONCAT('%', :namePart1, '%')))
        OR (:namePart2 IS NULL OR LOWER(t_ru.full_name) LIKE LOWER(CONCAT('%', :namePart2, '%')))
    )
    AND (:region IS NULL OR LOWER(t_ru.region) LIKE LOWER(CONCAT('%', :region, '%')))
    AND (:district IS NULL OR LOWER(t_ru.district) LIKE LOWER(CONCAT('%', :district, '%')))
    AND (:occupation IS NULL OR LOWER(t_ru.occupation) LIKE LOWER(CONCAT('%', :occupation, '%')))
    AND (:birthYear IS NULL OR p.birth_year = :birthYear)
    AND (:deathYear IS NULL OR p.death_year = :deathYear)
    AND (:birthDate IS NULL OR p.birth_date = :birthDate)
    AND (:deathDate IS NULL OR p.death_date = :deathDate)
    AND (:arrestDate IS NULL OR p.arrest_date = :arrestDate)
    AND (:sentenceDate IS NULL OR p.sentence_date = :sentenceDate)
    AND (:rehabilitationDate IS NULL OR p.rehabilitation_date = :rehabilitationDate)
    AND p.status IN (:allowedStatuses)
    GROUP BY p.id
    ORDER BY p.created_at DESC
    """, nativeQuery = true)
    Page<PersonSummaryProjection> findSummaryWithFilters(
            @Param("ruLang") short ruLang,
            @Param("kyLang") short kyLang,
            @Param("enLang") short enLang,
            @Param("trLang") short trLang,
            @Param("name") String name,
            @Param("namePart1") String namePart1,
            @Param("namePart2") String namePart2,
            @Param("region") String region,
            @Param("district") String district,
            @Param("occupation") String occupation,
            @Param("birthYear") Integer birthYear,
            @Param("deathYear") Integer deathYear,
            @Param("birthDate") LocalDate birthDate,
            @Param("deathDate") LocalDate deathDate,
            @Param("arrestDate") LocalDate arrestDate,
            @Param("sentenceDate") LocalDate sentenceDate,
            @Param("rehabilitationDate") LocalDate rehabilitationDate,
            @Param("allowedStatuses") List<Short> allowedStatuses,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Person p
    LEFT JOIN FETCH p.translations
    LEFT JOIN FETCH p.documents
    WHERE p.id = :id
    AND (p.status IN :allowedStatuses)
    """)
    Optional<Person> findByIdWithDetails(
            @Param("id") Long id,
            @Param("allowedStatuses") List<Short> allowedStatuses
    );
}


