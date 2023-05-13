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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

        @Operation(summary = "Returns a list of Books", description = "Returns a list of all books in database", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),

        })

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

        @Operation(summary = "returns a book by id", description = "returns book by the specified id", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "Successful request", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"title\": \"The Hitchhiker's Guide to the Galaxy\","
                                                        + "\"year\": 1979,"
                                                        + "\"price\": 42.0,"
                                                        + "\"PublishingCompany\": {"
                                                        + "\"publishingCompanyId\": 1,"
                                                        + "\"publishingCompanyName\": \"Megadodo Publications\""
                                                        + "},"
                                                        + "\"authors\": ["
                                                        + "{"
                                                        + "\"authorId\": 1,"
                                                        + "\"authorName\": \"Douglas Adams\""
                                                        + "}"
                                                        + "],"
                                                        + "\"storehouses\": ["
                                                        + "{"
                                                        + "\"storehouseId\": 1,"
                                                        + "\"storehouseCode\": \"321\""
                                                        + "},"
                                                        + "{"
                                                        + "\"storehouseId\": 2,"
                                                        + "\"storehouseCode\": \"123\""
                                                        + "}"
                                                        + "]"
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "book not found in database")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Object> searchById(@PathVariable Long id) {
                Optional<Book> book = this.bookService.findByid(id);

                return book.isPresent()
                                ? ResponseEntity.ok(new BookResponseDTO(book.get()))
                                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }

        @Operation(summary = "returns a book by title", description = "returns book by the specified title", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "Successful request", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"title\": \"The Hitchhiker's Guide to the Galaxy\","
                                                        + "\"year\": 1979,"
                                                        + "\"price\": 42.0,"
                                                        + "\"PublishingCompany\": {"
                                                        + "\"publishingCompanyId\": 1,"
                                                        + "\"publishingCompanyName\": \"Megadodo Publications\""
                                                        + "},"
                                                        + "\"authors\": ["
                                                        + "{"
                                                        + "\"authorId\": 1,"
                                                        + "\"authorName\": \"Douglas Adams\""
                                                        + "}"
                                                        + "],"
                                                        + "\"storehouses\": ["
                                                        + "{"
                                                        + "\"storehouseId\": 1,"
                                                        + "\"storehouseCode\": \"321\""
                                                        + "},"
                                                        + "{"
                                                        + "\"storehouseId\": 2,"
                                                        + "\"storehouseCode\": \"123\""
                                                        + "}"
                                                        + "]"
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "book not found in database")
        })

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

        @Operation(summary = "returns a list of books by author name", description = "returns a list of books by the specified author name", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "Successful request"),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "book not found in database")
        })

        @RequestMapping(value = "/search_by_author_name", method = RequestMethod.GET)
        public List<BookResponseDTO> searchByAuthorName(@RequestParam("author_name") String authorName) {

                return this.bookService
                                .findByAuthorName(authorName)
                                .stream()
                                .map(BookResponseDTO::new)
                                .collect(Collectors.toList());
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "record a new book", description = "save a new book in database", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "Book saved", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"title\": \"The Hitchhiker's Guide to the Galaxy\","
                                                        + "\"year\": 1979,"
                                                        + "\"price\": 42.0,"
                                                        + "\"PublishingCompany\": {"
                                                        + "\"publishingCompanyId\": 1,"
                                                        + "\"publishingCompanyName\": \"Megadodo Publications\""
                                                        + "},"
                                                        + "\"authors\": ["
                                                        + "{"
                                                        + "\"authorId\": 1,"
                                                        + "\"authorName\": \"Douglas Adams\""
                                                        + "}"
                                                        + "],"
                                                        + "\"storehouses\": ["
                                                        + "{"
                                                        + "\"storehouseId\": 1,"
                                                        + "\"storehouseCode\": \"321\""
                                                        + "},"
                                                        + "{"
                                                        + "\"storehouseId\": 2,"
                                                        + "\"storehouseCode\": \"123\""
                                                        + "}"
                                                        + "]"
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "book not found in database"),
                        @ApiResponse(responseCode = "409", description = "book title is already in use by other book in database")
        })

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

                Optional<Book> book = this.bookService.save(bookRequestDTO.toBook(),
                                bookRequestDTO.getQuantityInStorehouse());
                if (book.isPresent()) {
                        URI uri = uriBuilder.path("/storehouse/{id}").buildAndExpand(book.get().getId()).toUri();
                        return ResponseEntity.created(uri).body(new BookResponseDTO(book.get()));
                }

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entities not found in database");

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "delete a book by id", description = "delete a book by the specified id from database", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "book not found in database")
        })

        @DeleteMapping("/{id}")
        public ResponseEntity<Object> deleteById(@PathVariable Long id) {
                boolean removed = this.bookService.deleteByid(id);

                return removed ? ResponseEntity.status(HttpStatus.OK).body("Book was deleted")
                                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "updates a book", description = "updates data like title, author, year, etc", responses = {
                        @ApiResponse(responseCode = "500", ref = "InternalServerError"),
                        @ApiResponse(responseCode = "200", description = "Book updated", content = @Content(mediaType = "application/json", examples = {
                                        @ExampleObject(value = "{"
                                                        + "\"id\": 123,"
                                                        + "\"title\": \"The Hitchhiker's Guide to the Galaxy\","
                                                        + "\"year\": 1979,"
                                                        + "\"price\": 42.0,"
                                                        + "\"PublishingCompany\": {"
                                                        + "\"publishingCompanyId\": 1,"
                                                        + "\"publishingCompanyName\": \"Megadodo Publications\""
                                                        + "},"
                                                        + "\"authors\": ["
                                                        + "{"
                                                        + "\"authorId\": 1,"
                                                        + "\"authorName\": \"Douglas Adams\""
                                                        + "}"
                                                        + "],"
                                                        + "\"storehouses\": ["
                                                        + "{"
                                                        + "\"storehouseId\": 1,"
                                                        + "\"storehouseCode\": \"321\""
                                                        + "},"
                                                        + "{"
                                                        + "\"storehouseId\": 2,"
                                                        + "\"storehouseCode\": \"123\""
                                                        + "}"
                                                        + "]"
                                                        + "}")
                        })),
                        @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", description = "book not found in database"),
                        @ApiResponse(responseCode = "409", description = "book title is already in use by other book in database")
        })

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
