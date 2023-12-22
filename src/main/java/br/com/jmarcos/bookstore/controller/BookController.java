package br.com.jmarcos.bookstore.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import br.com.jmarcos.bookstore.controller.dto.book.BookRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.book.BookResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.book.BookUpdateDTO;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.enums.BookCategory;
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

      @Operation(summary = "Returns a list of Books", description = "Returns a list of all books in database. The returned list can be filtered by price, categories and release year", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "400", ref = "badRequest")

      })

      @Cacheable(value = "BookList")
      @GetMapping
      public List<BookResponseDTO> search(
                  @PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) Pageable pageable,
                  @RequestParam(name = "release_year", required = false) Integer year,
                  @RequestParam(name = "max_price", required = false) BigDecimal price,
                  @RequestParam(required = false) List<BookCategory> categories) {
            return this.bookService
                        .search(pageable, year, price, categories)
                        .map(BookResponseDTO::new)
                        .toList();
      }

      @Operation(summary = "returns a book by id", description = "returns book by the specified id", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })
      @GetMapping("/{id}")
      public ResponseEntity<BookResponseDTO> searchById(@PathVariable Long id) {
            Book book = this.bookService.findById(id);

            return ResponseEntity.ok(new BookResponseDTO(book));
      }

      @Operation(summary = "returns a book by title", description = "returns book by the specified title", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })

      @RequestMapping(value = "/search_by_title", method = RequestMethod.GET)
      public ResponseEntity<BookResponseDTO> searchByTitle(@RequestParam String title) {
            Book book = this.bookService.findByTitle(title);

            return ResponseEntity.ok(new BookResponseDTO(book));
      }

      @Operation(summary = "returns a list of books by author name", description = "returns a list of books by the specified author name", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
      })

      @RequestMapping(value = "/search_by_author_name", method = RequestMethod.GET)
      public List<BookResponseDTO> searchByAuthorName(@RequestParam("author_name") String authorName) {

            return this.bookService
                        .findByAuthorName(authorName)
                        .stream()
                        .map(BookResponseDTO::new)
                        .collect(Collectors.toList());
      }

      @Operation(summary = "record a new book", description = "save a new book in database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound"),
                  @ApiResponse(responseCode = "409", ref = "conflict")
      })

      @CacheEvict(value = "BookList", allEntries = true)
      @PostMapping
      public ResponseEntity<BookResponseDTO> save(@RequestBody @Valid BookRequestDTO bookRequestDTO,
                  UriComponentsBuilder uriBuilder) {
            Book book = this.bookService.save(bookRequestDTO.toBook(),
                        bookRequestDTO.getStorehouseBookDTOs()
                                    .stream()
                                    .map(storehouseBookDTO -> storehouseBookDTO.toStorehouseBook())
                                    .collect(Collectors.toList()));

            URI uri = uriBuilder.path("/storehouse/{id}").buildAndExpand(book.getId()).toUri();
            return ResponseEntity.created(uri).body(new BookResponseDTO(book));

      }

      @Operation(summary = "delete a book by id", description = "delete a book by the specified id from database", responses = {
                  @ApiResponse(responseCode = "204", description = "book was deleted"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })

      @DeleteMapping("/{id}")
      public ResponseEntity<Void> deleteById(@PathVariable Long id) {
            this.bookService.deleteById(id);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
      }

      @Operation(summary = "updates a book", description = "updates data like title, author, year, etc", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound"),
                  @ApiResponse(responseCode = "409", ref = "conflict")
      })

      @CacheEvict(value = "BookList", allEntries = true)
      @PutMapping("/{id}")
      public ResponseEntity<BookResponseDTO> update(@PathVariable Long id,
                  @RequestBody @Valid BookUpdateDTO bookUpdateDTO) {
            Book book = this.bookService.findById(id);

            book = this.bookService.updateBook(bookUpdateDTO.toBook(id),
                        bookUpdateDTO.getStorehouseBookDTOs()
                                    .stream()
                                    .map(storehouseBookDTO -> storehouseBookDTO.toStorehouseBook())
                                    .collect(Collectors.toList()));

            return ResponseEntity.ok(new BookResponseDTO(book));
      }

}
