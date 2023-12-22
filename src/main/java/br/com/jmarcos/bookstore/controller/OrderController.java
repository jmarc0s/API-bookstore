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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.bookstore.controller.dto.groceryCart.OrderRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.groceryCart.OrderResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.groceryCart.OrderUpdatebookDTO;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import br.com.jmarcos.bookstore.service.GroceryCartService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {
      private final GroceryCartService groceryCartService;

      @Autowired
      public OrderController(GroceryCartService groceryCartService) {
            this.groceryCartService = groceryCartService;
      }

      @Operation(summary = "Returns a list of Grocery carts", description = "Returns a list of all of your Grocery Carts  in database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied")

      })

      @GetMapping
      public List<OrderResponseDTO> search(@AuthenticationPrincipal Person person) {
            List<GroceryCart> orders = this.groceryCartService.searchByPersonId(person.getId());
            List<OrderResponseDTO> orderResponse = new ArrayList<>();

            for (GroceryCart order : orders) {
                  List<GroceryCartBook> orderBook = this.groceryCartService
                              .listGroceryCartBook(order);
                  OrderResponseDTO orderResponseDTO = new OrderResponseDTO(orderBook);
                  orderResponse.add(orderResponseDTO);

            }

            return orderResponse.stream().collect(Collectors.toList());
      }

      @Operation(summary = "returns a grocery cart by id", description = "returns grocery cart by the specified id", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })

      @GetMapping("/{id}")
      public ResponseEntity<OrderResponseDTO> searchById(@PathVariable Long id,
                  @AuthenticationPrincipal Person person) {

            return ResponseEntity
                        .ok(new OrderResponseDTO(
                                    this.groceryCartService.searchInfoOrderByIdAndPersonId(id, person.getId())));

      }

      @GetMapping("test")
      public ResponseEntity<OrderResponseDTO> teste(
                  @AuthenticationPrincipal Person person) {

            return ResponseEntity
                        .ok(new OrderResponseDTO(
                                    this.groceryCartService.searchGroceryCartInfor(person.getId())));

      }

      @Operation(summary = "delete a grocery cart by id", description = "delete a grocery cart by the specified id from database", responses = {
                  @ApiResponse(responseCode = "204", description = "order was deleted"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })

      @DeleteMapping("/{id}")
      public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Person person) {
            this.groceryCartService.deleteByIdAndPersonId(id, person.getId());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

      }

      @Operation(summary = "create a new grocery cart", description = "add a new grocery cart in database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })

      // FIXME
      // retirar logica do controller
      @PostMapping
      public ResponseEntity<OrderResponseDTO> save(
                  @RequestBody @Valid OrderRequestDTO orderRequestDTO,
                  UriComponentsBuilder uriBuilder,
                  @AuthenticationPrincipal Person person) {
            GroceryCart groceryCart;
            // VERIFICAR SE EXISTE UM PEDIDO ABERTO, SE HOUVER PEDIDO ABERTO, APENAS
            // ADICIONAR LIVROS A ELE

            Optional<GroceryCart> openOrder = this.groceryCartService.findOpenOrder(person.getId());

            List<GroceryCartBook> groceryCartBooks = orderRequestDTO.getBooks()
                        .stream()
                        .map(groceryCartBookDTO -> groceryCartBookDTO.toGroceryCartBook())
                        .collect(Collectors.toList());

            if (openOrder.isEmpty()) {

                  groceryCart = this.groceryCartService.save(orderRequestDTO.toGroceryCart(person.getId()),
                              groceryCartBooks);

                  List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);

                  URI uri = uriBuilder.path("/orders/{id}").buildAndExpand(groceryCart.getId()).toUri();
                  return ResponseEntity.created(uri)
                              .body(new OrderResponseDTO(groceryCartBook));

            }

            groceryCart = this.groceryCartService.addBooks(openOrder.get(), groceryCartBooks);

            List<GroceryCartBook> groceryCartBook = this.groceryCartService.listGroceryCartBook(groceryCart);
            return ResponseEntity.ok(new OrderResponseDTO(groceryCartBook));

      }

      @Operation(summary = "delete a book by book id", description = "delete a book of your grocery cart by book id", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })
      // FIXME
      // consertar os parametros na url
      @DeleteMapping("/{id}/books/{bookId}")
      public ResponseEntity<OrderResponseDTO> deleteBook(@PathVariable Long id, @PathVariable Long bookId,
                  @AuthenticationPrincipal Person person) {
            GroceryCart order = this.groceryCartService.searchByIdAndPersonId(id, person.getId());

            order = this.groceryCartService.deleteBook(order, bookId);
            List<GroceryCartBook> orderBook = this.groceryCartService.listGroceryCartBook(order);

            return ResponseEntity.ok(new OrderResponseDTO(orderBook));

      }

      @Operation(summary = "update a book by id", description = "update data like book quantity on your grocery cart", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })
      @PutMapping("/{id}/books")
      public ResponseEntity<OrderResponseDTO> updateBook(@PathVariable Long id,
                  @RequestBody @Valid OrderUpdatebookDTO orderUpdatebookDTO,
                  @AuthenticationPrincipal Person person) {
            GroceryCart order = this.groceryCartService.searchByIdAndPersonId(id, person.getId());

            order = this.groceryCartService.updateBook(order, orderUpdatebookDTO.getBookId(),
                        orderUpdatebookDTO.getQuantity());

            List<GroceryCartBook> orderBooks = this.groceryCartService.listGroceryCartBook(order);
            return ResponseEntity.ok(new OrderResponseDTO(orderBooks));

      }
}
