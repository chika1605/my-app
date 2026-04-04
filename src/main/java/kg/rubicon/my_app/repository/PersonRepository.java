package kg.rubicon.my_app.repository;

import kg.rubicon.my_app.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
        SELECT 1 FROM unnest(CAST(:names AS text[])) AS input_name
        WHERE input_name LIKE CONCAT(p.normalized_name, '%')
        OR p.normalized_name LIKE CONCAT(input_name, '%')
    )
    """, nativeQuery = true)
    List<Person> findByNormalizedNamesBidirectional(@Param("names") String names);
}
