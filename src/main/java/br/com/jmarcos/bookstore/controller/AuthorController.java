package br.com.jmarcos.bookstore.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import br.com.jmarcos.bookstore.controller.dto.author.AuthorRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.author.AuthorResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.author.AuthorUpdateDTO;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.service.AuthorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/authors")
public class AuthorController {
        private final AuthorService authorService;

        @Autowired
        public AuthorController(AuthorService authorService) {
                this.authorService = authorService;
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "Returns a list of Authors", description = "Returns a list of all authors in database", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),

        })
        @Cacheable(value = "AuthorList")
        @GetMapping
        public Page<AuthorResponseDTO> search(Pageable pageable) {
                return this.authorService
                                .search(pageable)
                                .map(AuthorResponseDTO::new);
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "record a new Author", description = "save a author in database", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "author saved", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"name\": \"J. K. Rowling\","
                                                        + "\"address\": {"
                                                        + "\"id\": \"123\","
                                                        + "\"street\": \"123 Main St\","
                                                        + "\"number\": \"123\","
                                                        + "\"city\": \"Anytown\","
                                                        + "\"state\": \"CA\","
                                                        + "\"zip\": \"12345\""
                                                        + "},"
                                                        + "\"url\": \"jk.com\""
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "409", description = "author name is already in use by other Storehouse in database")
        })

        @CacheEvict(value = "AuthorList", allEntries = true)
        @PostMapping
        public ResponseEntity<Object> save(@RequestBody @Valid AuthorRequestDTO authorRequestDTO,
                        UriComponentsBuilder uriBuilder) {

                Author author = this.authorService.save(authorRequestDTO.toAuthor());

                URI uri = uriBuilder.path("/authors/{id}").buildAndExpand(author.getId()).toUri();
                return ResponseEntity.created(uri).body(new AuthorResponseDTO(author));
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "returns an author by id", description = "returns an author by the specified id", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "Successful request", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"name\": \"J. K. Rowling\","
                                                        + "\"address\": {"
                                                        + "\"id\": \"123\","
                                                        + "\"street\": \"123 Main St\","
                                                        + "\"number\": \"123\","
                                                        + "\"city\": \"Anytown\","
                                                        + "\"state\": \"CA\","
                                                        + "\"zip\": \"12345\""
                                                        + "},"
                                                        + "\"url\": \"jk.com\""
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "author not found")
        })

        @GetMapping("/{id}")
        public ResponseEntity<Object> searchById(@PathVariable Long id) {
                Author author = this.authorService.searchById(id);

                return ResponseEntity.ok(new AuthorResponseDTO(author));
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "returns an author by name", description = "returns an author by the specified name", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "Successful request", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"name\": \"J. K. Rowling\","
                                                        + "\"address\": {"
                                                        + "\"id\": \"123\","
                                                        + "\"street\": \"123 Main St\","
                                                        + "\"number\": \"123\","
                                                        + "\"city\": \"Anytown\","
                                                        + "\"state\": \"CA\","
                                                        + "\"zip\": \"12345\""
                                                        + "},"
                                                        + "\"url\": \"jk.com\""
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "author not found in database")
        })

        @RequestMapping(value = "/search_by_name", method = RequestMethod.GET)
        public ResponseEntity<Object> searchByName(@RequestParam String name) {
                Author author = this.authorService.searchByName(name);

                return ResponseEntity.ok(new AuthorResponseDTO(author));

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "delete an author by id", description = "delete a author by the specified id from database", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "author not found in database")
        })

        @DeleteMapping("/{id}")
        public ResponseEntity<Object> deleteById(@PathVariable Long id) {
                this.authorService.deleteById(id);

                return  ResponseEntity.status(HttpStatus.OK).body("Author was deleted");
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "update an author", description = "update data like name, url, etc", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "author updated", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"name\": \"J. K. Rowling\","
                                                        + "\"address\": {"
                                                        + "\"id\": \"123\","
                                                        + "\"street\": \"123 Main St\","
                                                        + "\"number\": \"123\","
                                                        + "\"city\": \"Anytown\","
                                                        + "\"state\": \"CA\","
                                                        + "\"zip\": \"12345\""
                                                        + "},"
                                                        + "\"url\": \"jk.com\""
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "author not found"),
                        @ApiResponse(responseCode = "409", description = "author name is already in use by other Author in database")
        })

        @CacheEvict(value = "AuthorList", allEntries = true)
        @PutMapping("/{id}")
        public ResponseEntity<Object> update(@RequestBody @Valid AuthorUpdateDTO authorUpdateDTO,
                        @PathVariable Long id) {

                Author author = this.authorService.update(authorUpdateDTO.toAuthor(id));

                return ResponseEntity.ok(new AuthorResponseDTO(author));
        }
}
