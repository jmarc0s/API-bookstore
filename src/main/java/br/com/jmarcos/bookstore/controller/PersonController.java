package br.com.jmarcos.bookstore.controller;

import java.net.URI;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/persons")
public class PersonController {
        private final PersonService personService;

        @Autowired
        public PersonController(PersonService personService) {
                this.personService = personService;
        }

        @Operation(summary = "record a new  profile", description = "save a new profile in database. With a profile, you can buy books", responses = {
                        @ApiResponse(responseCode = "201", description = "created"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "409", ref = "conflict"),
        })

        @PostMapping
        public ResponseEntity<Object> save(@RequestBody @Valid PersonRequestDTO personRequestDTO,
                        UriComponentsBuilder uriBuilder) {

                Person person = personRequestDTO.toPerson();
                person = this.personService.save(person);
                URI uri = uriBuilder.path("/persons/{id}").buildAndExpand(person.getId()).toUri();
                return ResponseEntity.created(uri).body(new PersonResponseDTO(person));
        }

        @PostMapping("/confirm_code")
        public ResponseEntity<Object> confirmCode(@RequestParam String email, @RequestParam String code) {
                return ResponseEntity.ok(this.personService.confirmCode(email, code));
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "update your profile data", description = "update data like email, name ", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "409", ref = "conflict") })

        @PutMapping("/profile")
        public ResponseEntity<Object> updateProfile(@RequestBody PersonUpdateDTO personUpdateDTO,
                        @AuthenticationPrincipal Person personRequest) {
                Person person = this.personService.searchById(personRequest.getId());

                person = this.personService.update(personUpdateDTO.toPerson(person.getId()));

                return ResponseEntity.ok(new PersonResponseDTO(person));
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "delete your profile", description = "delete all your profile data", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
        })

        @DeleteMapping("/profile")
        public ResponseEntity<Object> deleteProfile(@AuthenticationPrincipal Person person) {
                this.personService.deleteById(person.getId());

                return ResponseEntity.status(HttpStatus.OK).body("profile was deleted");

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "get your profile data", description = "show your profile data", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "409", ref = "conflict") })

        @GetMapping("/profile")
        public ResponseEntity<PersonResponseDTO> getProfileData(@AuthenticationPrincipal Person person) {
                Person user = this.personService.searchById(person.getId());
                return ResponseEntity.ok(new PersonResponseDTO(user));
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "list all profiles in database", description = "returns a list of all pessoal data in database", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied")
        })

        @GetMapping
        public Page<PersonResponseDTO> search(
                        @PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) Pageable pageable) {
                return this.personService
                                .search(pageable)
                                .map(PersonResponseDTO::new);
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "Returns a profile by id", description = "Returns a profile with the  specified id", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Object> searchById(@PathVariable Long id) {
                Person person = this.personService.searchById(id);

                return ResponseEntity.ok(new PersonResponseDTO(person));

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "Returns a profile by email", description = "Returns a profile with the specified email", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })

        @RequestMapping(value = "/search_by_email", method = RequestMethod.GET)
        public ResponseEntity<Object> searchByEmail(@RequestParam String email) {
                Person person = this.personService.searchByEmail(email);

                return ResponseEntity.ok(new PersonResponseDTO(person));

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "delete a profile by id", description = "delete any profile by the specified id", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })

        @DeleteMapping("/{id}")
        public ResponseEntity<Object> deleteById(@PathVariable Long id) {
                this.personService.deleteById(id);

                return ResponseEntity.status(HttpStatus.OK).body("Person was deleted");
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "set profile permission", description = "add a permission to a profile", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })

        @PatchMapping("/{personId}")
        public ResponseEntity<Object> setPermission(@PathVariable Long personId, @RequestParam String permission) {
                Person person = this.personService.searchById(personId);

                person = this.personService.addPermission(person, permission);

                return ResponseEntity.ok(new PersonResponseDTO(person));

        }
}
