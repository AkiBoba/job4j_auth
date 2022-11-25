package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.dto.PersonDTO;
import ru.job4j.repository.PersonRepository;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;
    private BCryptPasswordEncoder encoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());

    private final ObjectMapper objectMapper;

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
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
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
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (persons.findById(newperson.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Проверьте передаваемые параметры");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (persons.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Проверьте передаваемые параметры");
        }
        Person person = new Person();
        person.setId(id);
        persons.delete(person);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (!person.getPassword().contains("A")) {
            throw new IllegalArgumentException("Invalid password. Password must contains char A.");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonDTO> patch(@PathVariable int id, @RequestBody PersonDTO personDTO) {
        var person = persons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user with this id"));
        person.setLogin(personDTO.getLogin());
        person.setPassword(personDTO.getPassword());
        return ResponseEntity.of(Optional.of(new PersonDTO(person.getLogin(), person.getPassword())));
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {{
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}
