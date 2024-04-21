package fam.sub.repository;

import fam.sub.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByName(String name);

    List<Person> findAllByOrderByBalance();
}
