package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

import br.com.jmarcos.bookstore.controller.dto.book.BookRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.book.BookResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.book.BookUpdateDTO;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.service.BookService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Cacheable(value = "BookList")
    @GetMapping
    public Page<BookResponseDTO> search(
            @PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) Pageable pageable) {
        return this.bookService
                .search(pageable)
                // .stream()
                .map(BookResponseDTO::new)
        /* .collect(Collectors.toList()) */;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> searchById(@PathVariable Long id) {
        Optional<Book> book = this.bookService.findByid(id);

        return book.isPresent()
                ? ResponseEntity.ok(new BookResponseDTO(book.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
    }

    @RequestMapping(value = "/search_by_title", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByTitle(@RequestParam String title) {
        Optional<Book> book = this.bookService.findByTitle(title);

        return book.isPresent()
                ? ResponseEntity.ok(new BookResponseDTO(book.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
    }

    // @RequestMapping(value = "/teste", method = RequestMethod.GET)
    // public ResponseEntity<Object> testeSql(@RequestParam int pc) {
    // Optional<Book> book = this.bookService.testeSql(pc);
    //
    // return book.isPresent()
    // ? ResponseEntity.ok(new BookResponseDTO(book.get()))
    // : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
    // }

    @RequestMapping(value = "/search_by_author_name", method = RequestMethod.GET)
    public List<BookResponseDTO> searchByAuthorName(@RequestParam("author_name") String authorName) {

        return this.bookService
                .findByAuthorName(authorName)
                .stream()
                .map(BookResponseDTO::new)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "BookList", allEntries = true)
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid BookRequestDTO bookRequestDTO,
            UriComponentsBuilder uriBuilder) {

        Boolean exists = this.bookService.existsByTitle(bookRequestDTO.getTitle());

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book title is already in use");
        }

        if (!bookRequestDTO.verifyCompatibility()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The number of quantities should match the number of storehouse ids.");
        }

        Optional<Book> book = this.bookService.save(bookRequestDTO.toBook(), bookRequestDTO.getQuantityInStorehouse());
        if (book.isPresent()) {
            URI uri = uriBuilder.path("/storehouse/{id}").buildAndExpand(book.get().getId()).toUri();
            return ResponseEntity.created(uri).body(new BookResponseDTO(book.get()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entities not found in database");

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        boolean removed = this.bookService.deleteByid(id);

        return removed ? ResponseEntity.status(HttpStatus.OK).body("Book was deleted")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
    }

    @CacheEvict(value = "BookList", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody @Valid BookUpdateDTO bookUpdateDTO) {

        Optional<Book> book = this.bookService.findByid(id);
        if (book.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }

        if (bookUpdateDTO.getStorehouseIdList().size() != bookUpdateDTO.getQuantityInStorehouse().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The number of quantities should match the number of storehouse ids.");
        }

        if (!Objects.equals(book.get().getTitle(), bookUpdateDTO.getTitle())
                && bookService.existsByTitle(bookUpdateDTO.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book title is already in use.");
        }

        book = this.bookService.updateBook(bookUpdateDTO.toBook(id), bookUpdateDTO.getQuantityInStorehouse());
        return book.isPresent()
                ? ResponseEntity.ok(new BookResponseDTO(book.get()))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entities not found in database");
    }

}
