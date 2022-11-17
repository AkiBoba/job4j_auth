package ru.job4j.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.repository.PersonRepository;
import ru.job4j.service.PersonService;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;

    @GetMapping("/")
    public List<Person> findAll() {
        return persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) throws Exception {
        var person = persons.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не существует такого id");
        }
        return new ResponseEntity<>(
                person.get(), HttpStatus.OK
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        var newperson = persons.save(person);
        if (newperson.equals(null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Проверьте передаваемые параметры");
        }
        return new ResponseEntity<>(
                newperson,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        var newperson = persons.save(person);
        if (newperson.equals(null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Проверьте передаваемые параметры");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (persons.findById(id).isPresent()) {
            Person person = new Person();
            person.setId(id);
            persons.delete(person);
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Проверьте передаваемые параметры");
        }
    }
}
