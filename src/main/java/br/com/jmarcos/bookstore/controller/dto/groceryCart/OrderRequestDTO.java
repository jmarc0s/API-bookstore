package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import java.util.HashSet;
import java.util.Set;

import br.com.jmarcos.bookstore.controller.dto.GroceryCartBookDTO.GroceryCartBookDTO;
import br.com.jmarcos.bookstore.controller.dto.book.BookRequestDTO;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.model.enums.OrderStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDTO {

      @Valid
      @NotEmpty
      private Set<GroceryCartBookDTO> books = new HashSet<>();

      public GroceryCart toGroceryCart(Long personId) {
            GroceryCart order = new GroceryCart();

            for (GroceryCartBookDTO groceryCartBookDTO : books) {
                  Book book = BookRequestDTO.toBook(groceryCartBookDTO.getBookId());
                  order.getBooks().add(book);
            }
            order.setPerson(new Person(personId));
            order.setOrderSTatus(OrderStatusEnum.OPEN);

            return order;
      }

}
