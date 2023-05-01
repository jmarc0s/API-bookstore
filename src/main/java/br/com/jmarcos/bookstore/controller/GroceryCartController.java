package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import br.com.jmarcos.bookstore.controller.dto.groceryCart.GroceryCartAddbookDTO;
import br.com.jmarcos.bookstore.controller.dto.groceryCart.GroceryCartRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.groceryCart.GroceryCartResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.groceryCart.GroceryCartUpdatebookDTO;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import br.com.jmarcos.bookstore.service.GroceryCartService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/grocery_cart")
public class GroceryCartController {
    private final GroceryCartService groceryCartService;

    @Autowired
    public GroceryCartController(GroceryCartService groceryCartService) {
        this.groceryCartService = groceryCartService;
    }

    @GetMapping
    public List<GroceryCartResponseDTO> search(@AuthenticationPrincipal Person person) {
        List<GroceryCart> groceryCarts = this.groceryCartService.searchPersonId(person.getId());
        List<GroceryCartResponseDTO> groceryCartsResponse = new ArrayList<>();

        for (GroceryCart groceryCart : groceryCarts) {
            List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);
            GroceryCartResponseDTO groceryCartResponseDTO = new GroceryCartResponseDTO(groceryCart, groceryCartBook);
            groceryCartsResponse.add(groceryCartResponseDTO);

        }

        return groceryCartsResponse.stream().collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> searchByid(@PathVariable Long id, @AuthenticationPrincipal Person person) {
        Optional<GroceryCart> groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());

        if (groceryCart.isPresent()) {
            List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart.get());
            return ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart.get(), groceryCartBook));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("groceryCart not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id, @AuthenticationPrincipal Person person) {
        boolean removed = this.groceryCartService.deleteByIdAndPersonId(id, person.getId());
        return removed ? ResponseEntity.status(HttpStatus.OK).body(
                "GroceryCart was deleted")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "GroceryCart Not Found");
    }

    @PostMapping
    public ResponseEntity<Object> save(
            @RequestBody(required = false) @Valid GroceryCartRequestDTO groceryCartRequestDTO,
            UriComponentsBuilder uriBuilder,
            @AuthenticationPrincipal Person person) {
        Optional<GroceryCart> groceryCart;

        if (groceryCartRequestDTO != null) {
            if (!groceryCartRequestDTO.verifyCompatibility()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The number of quantities should match the number of book ids.");
            }
            groceryCart = this.groceryCartService.save(groceryCartRequestDTO.toGroceryCart(person.getId()),
                    groceryCartRequestDTO.getQuantities());
        } else {
            groceryCart = this.groceryCartService.save(person.getId());
        }

        if (groceryCart.isPresent()) {
            List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart.get());
            URI uri = uriBuilder.path("/grocery_cart/{id}").buildAndExpand(groceryCart.get().getId()).toUri();
            return ResponseEntity.created(uri).body(new GroceryCartResponseDTO(groceryCart.get(), groceryCartBook));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Books not found in database");

    }

    @PostMapping("/{id}/books")
    public ResponseEntity<Object> addBook(@PathVariable Long id,
            @RequestBody @Valid GroceryCartAddbookDTO groceryCartAddbookDTO, @AuthenticationPrincipal Person person) {
        Optional<GroceryCart> groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());
        if (groceryCart.isPresent()) {
            groceryCart = this.groceryCartService.addBook(groceryCart.get(), groceryCartAddbookDTO.getBookId(),
                    groceryCartAddbookDTO.getQuantity());
            List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart.get());

            return groceryCart.isPresent()
                    ? ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart.get(), groceryCartBook))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("The specified book was not found or book is already in grocery cart");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GroceryCart not Found");
    }

    @DeleteMapping("/{id}/books/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long id, @PathVariable Long bookId,
            @AuthenticationPrincipal Person person) {
        Optional<GroceryCart> groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());
        if (groceryCart.isPresent()) {
            groceryCart = this.groceryCartService.deleteBook(groceryCart.get(), bookId);
            List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart.get());

            return groceryCart.isPresent()
                    ? ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart.get(), groceryCartBook))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("The specified book was not found.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GroceryCart not Found");
    }

    @PutMapping("/{id}/books")
    public ResponseEntity<Object> updateBook(@PathVariable Long id,
            @RequestBody @Valid GroceryCartUpdatebookDTO groceryCartUpdatebookDTO,
            @AuthenticationPrincipal Person person) {
        Optional<GroceryCart> groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());
        if (groceryCart.isPresent()) {

            groceryCart = this.groceryCartService.updateBook(groceryCart.get(), groceryCartUpdatebookDTO.getBookId(),
                    groceryCartUpdatebookDTO.getQuantity());
            List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart.get());

            return groceryCart.isPresent()
                    ? ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart.get(), groceryCartBook))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("The specified book was not found in database or in grocery cart.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GroceryCart not Found");
    }
}
