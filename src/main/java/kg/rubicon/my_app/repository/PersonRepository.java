package kg.rubicon.my_app.repository;

import kg.rubicon.my_app.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {}
