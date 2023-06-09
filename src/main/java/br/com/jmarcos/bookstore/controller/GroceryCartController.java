package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "Returns a list of Grocery carts", description = "Returns a list of all of your Grocery Carts  in database", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied")

        })

        @GetMapping
        public List<GroceryCartResponseDTO> search(@AuthenticationPrincipal Person person) {
                List<GroceryCart> groceryCarts = this.groceryCartService.searchByPersonId(person.getId());
                List<GroceryCartResponseDTO> groceryCartsResponse = new ArrayList<>();

                for (GroceryCart groceryCart : groceryCarts) {
                        List<GroceryCartBook> groceryCartBook = this.groceryCartService
                                        .listGroceryCartBook(groceryCart);
                        GroceryCartResponseDTO groceryCartResponseDTO = new GroceryCartResponseDTO(groceryCart,
                                        groceryCartBook);
                        groceryCartsResponse.add(groceryCartResponseDTO);

                }

                return groceryCartsResponse.stream().collect(Collectors.toList());
        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "returns a grocery cart by id", description = "returns grocery cart by the specified id", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })

        @GetMapping("/{id}")
        public ResponseEntity<Object> searchById(@PathVariable Long id, @AuthenticationPrincipal Person person) {
                GroceryCart groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());

                List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);

                return ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart, groceryCartBook));

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "delete a grocery cart by id", description = "delete a grocery cart by the specified id from database", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })

        @DeleteMapping("/{id}")
        public ResponseEntity<Object> delete(@PathVariable Long id, @AuthenticationPrincipal Person person) {
                this.groceryCartService.deleteByIdAndPersonId(id, person.getId());

                return ResponseEntity.status(HttpStatus.OK).body("GroceryCart was deleted");

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "create a new grocery cart", description = "add a new grocery cart in database", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })

        @PostMapping
        public ResponseEntity<Object> save(
                        @RequestBody @Valid GroceryCartRequestDTO groceryCartRequestDTO,
                        UriComponentsBuilder uriBuilder,
                        @AuthenticationPrincipal Person person) {
                GroceryCart groceryCart;

                //VERIFICAR SE EXISTE UM PEDIDO ABERTO, se HOUVER PEDIDO ABERTO, APENAS ADICIONAR LIVROS A ELE

                groceryCart = this.groceryCartService.save(groceryCartRequestDTO.toGroceryCart(person.getId()),
                                groceryCartRequestDTO.getBooks()
                                .stream()
                                .map(groceryCartBookDTO -> groceryCartBookDTO.toGroceryCart())
                                .collect(Collectors.toList()));
                

                List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);

                URI uri = uriBuilder.path("/grocery_cart/{id}").buildAndExpand(groceryCart.getId()).toUri();
                return ResponseEntity.created(uri).body(new GroceryCartResponseDTO(groceryCart, groceryCartBook));

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "add a new book on your grocery cart", description = "add a new book on your grocery cart", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })

        @PostMapping("/{id}/books")
        public ResponseEntity<Object> addBook(@PathVariable Long id,
                        @RequestBody @Valid GroceryCartAddbookDTO groceryCartAddbookDTO,
                        @AuthenticationPrincipal Person person) {
                GroceryCart groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());

                groceryCart = this.groceryCartService.addBook(groceryCart, groceryCartAddbookDTO.getBookId(),
                                groceryCartAddbookDTO.getQuantity());

                List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);
                return ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart, groceryCartBook));

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "delete a book by book id", description = "delete a book of your grocery cart by book id", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })
        @DeleteMapping("/{id}/books/{bookId}")
        public ResponseEntity<Object> deleteBook(@PathVariable Long id, @PathVariable Long bookId,
                        @AuthenticationPrincipal Person person) {
                GroceryCart groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());

                groceryCart = this.groceryCartService.deleteBook(groceryCart, bookId);
                List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);

                return ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart, groceryCartBook));

        }

        @SecurityRequirement(name = "Authorization")
        @Operation(summary = "update a book by id", description = "update data like book quantity on your grocery cart", responses = {
                        @ApiResponse(responseCode = "200", ref = "ok"),
                        @ApiResponse(responseCode = "400", ref = "badRequest"),
                        @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                        @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
        })
        @PutMapping("/{id}/books")
        public ResponseEntity<Object> updateBook(@PathVariable Long id,
                        @RequestBody @Valid GroceryCartUpdatebookDTO groceryCartUpdatebookDTO,
                        @AuthenticationPrincipal Person person) {
                GroceryCart groceryCart = this.groceryCartService.searchByIdAndPersonId(id, person.getId());

                groceryCart = this.groceryCartService.updateBook(groceryCart, groceryCartUpdatebookDTO.getBookId(),
                                groceryCartUpdatebookDTO.getQuantity());

                List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);
                return ResponseEntity.ok(new GroceryCartResponseDTO(groceryCart, groceryCartBook));

        }
}
