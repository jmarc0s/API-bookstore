package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

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

    @Cacheable(value = "AuthorList")
    @GetMapping
    public Page<AuthorResponseDTO> search(Pageable pageable) {
        return this.authorService
                .search(pageable)
                .map(AuthorResponseDTO::new);
    }

    @CacheEvict(value = "AuthorList", allEntries = true)
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid AuthorRequestDTO authorRequestDTO,
            UriComponentsBuilder uriBuilder) {
        if (authorService.existsByName(authorRequestDTO.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Author name is already in use");
        }

        Author author = this.authorService.save(authorRequestDTO.toAuthor());

        URI uri = uriBuilder.path("/authors/{id}").buildAndExpand(author.getId()).toUri();
        return ResponseEntity.created(uri).body(new AuthorResponseDTO(author));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> searchById(@PathVariable Long id) {
        Optional<Author> author = this.authorService.searchById(id);

        return author.isPresent()
                ? ResponseEntity.ok(new AuthorResponseDTO(author.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found");
    }

    @RequestMapping(value = "/search_by_name", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByName(@RequestParam String name) {
        Optional<Author> author = this.authorService.searchByName(name);

        return author.isPresent()
                ? ResponseEntity.ok(new AuthorResponseDTO(author.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        boolean removed = this.authorService.deleteById(id);
        return removed ? ResponseEntity.status(HttpStatus.OK).body(
                "Author was deleted")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "Author Not Found");
    }

    @CacheEvict(value = "AuthorList", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody @Valid AuthorUpdateDTO authorUpdateDTO, @PathVariable Long id) {
        Optional<Author> author = this.authorService.searchById(id);
        if (author.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found");
        }
        if (!Objects.equals(author.get().getName(), authorUpdateDTO.getName())
                && authorService.existsByName(authorUpdateDTO.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Author name is already in use");
        }

        author = this.authorService.update(authorUpdateDTO.toAuthor(id));

        return author.isPresent()
                ? ResponseEntity.ok(new AuthorResponseDTO(author.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found");
    }
}
