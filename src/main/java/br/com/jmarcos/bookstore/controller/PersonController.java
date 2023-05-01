package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.Optional;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import br.com.jmarcos.bookstore.controller.dto.person.PersonRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.person.PersonResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.person.PersonUpdateDTO;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.service.PersonService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    // REGISTER A PERSON //

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid PersonRequestDTO personRequestDTO,
            UriComponentsBuilder uriBuilder) {
        if (this.personService.existsByEmail(personRequestDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }

        Person person = personRequestDTO.toPerson();
        this.personService.save(person);
        URI uri = uriBuilder.path("/publishingCompany/{id}").buildAndExpand(person.getId()).toUri();
        return ResponseEntity.created(uri).body(new PersonResponseDTO(person));
    }

    // USER TOOLS //

    @PutMapping("/profile")
    public ResponseEntity<Object> updateProfile(@RequestBody PersonUpdateDTO personUpdateDTO,
            @AuthenticationPrincipal Person personRequest) {
        Optional<Person> person = this.personService.searchById(personRequest.getId());

        if (!Objects.equals(person.get().getEmail(), personUpdateDTO.getEmail())
                && personService.existsByEmail(personUpdateDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book title is already in use.");
        }

        person = this.personService.update(person.get(), personUpdateDTO.toPerson());
        return person.isPresent()
                ? ResponseEntity.ok(new PersonResponseDTO(person.get()))
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: something went wrong, profile was not deleted");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Object> deleteProfile(@AuthenticationPrincipal Person person) {
        boolean removed = this.personService.deleteByid(person.getId());

        return removed ? ResponseEntity.status(HttpStatus.OK).body("profile was deleted")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: something went wrong, profile was not deleted");
    }

    @GetMapping("/profile")
    public ResponseEntity<PersonResponseDTO> getProfileData(@AuthenticationPrincipal Person person) {
        Optional<Person> user = this.personService.searchById(person.getId());
        return ResponseEntity.ok(new PersonResponseDTO(user.get()));
    }

    // ADMIN TOOLS //

    @GetMapping
    public Page<PersonResponseDTO> search(
            @PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) Pageable pageable) {
        return this.personService
                .search(pageable)
                .map(PersonResponseDTO::new);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> searchById(@PathVariable Long id) {
        Optional<Person> person = this.personService.searchById(id);

        return person.isPresent()
                ? ResponseEntity.ok(new PersonResponseDTO(person.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("person not found");
    }

    @RequestMapping(value = "/search_by_email", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByEmail(@RequestParam String email) {
        Optional<Person> person = this.personService.searchByEmail(email);

        return person.isPresent()
                ? ResponseEntity.ok(new PersonResponseDTO(person.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("person not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        boolean removed = this.personService.deleteByid(id);

        return removed ? ResponseEntity.status(HttpStatus.OK).body("Person was deleted")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found");
    }

    @PatchMapping("/{personId}")
    public ResponseEntity<Object> setPermission(@PathVariable Long personId, @RequestParam String permission) {
        Optional<Person> person = this.personService.searchById(personId);

        if (person.isPresent()) {
            person = this.personService.addPermission(person.get(), permission);
            return person.isPresent()
                    ? ResponseEntity.ok(new PersonResponseDTO(person.get()))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("permission not found");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found");
    }
}
