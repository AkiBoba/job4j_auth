package ru.job4j.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.job4j.domain.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    @Query("SELECT p FROM Person p WHERE p.login = :login")
    Person findByLogin(String login);
}
