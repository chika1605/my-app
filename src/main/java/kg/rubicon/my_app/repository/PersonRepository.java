package kg.rubicon.my_app.repository;

import kg.rubicon.my_app.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Проверка дубля: совпадение по имени (normalizedName) и году рождения
     */
    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM Person p
            JOIN p.translations t
            WHERE t.normalizedName = :normalizedName
            AND p.birthYear = :birthYear
            AND t.language = :language
            """)
    boolean existsByNormalizedNameAndBirthYearAndLanguage(
            @Param("normalizedName") String normalizedName,
            @Param("birthYear") Integer birthYear,
            @Param("language") short language
    );

    /**
     * Поиск по имени + фильтры (регион, язык, статус)
     */
    @Query("""
            SELECT DISTINCT p FROM Person p
            JOIN p.translations t
            WHERE t.language = :language
            AND (:name IS NULL OR t.normalizedName LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:region IS NULL OR t.region = :region)
            AND (:status IS NULL OR p.status = :status)
            ORDER BY p.createdAt DESC
            """)
    List<Person> search(
            @Param("language") short language,
            @Param("name") String name,
            @Param("region") String region,
            @Param("status") Short status
    );

    /**
     * Получить одного со всеми переводами
     */
    @Query("""
            SELECT p FROM Person p
            JOIN FETCH p.translations
            WHERE p.id = :id
            """)
    Optional<Person> findByIdWithTranslations(@Param("id") Long id);
}
