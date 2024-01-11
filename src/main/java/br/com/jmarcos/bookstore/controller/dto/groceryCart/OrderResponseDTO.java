package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import java.util.ArrayList;
import java.util.List;

import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
      private Long id;
      private Long person_id;
      private String status;
      private List<OrderListBookDTO> books = new ArrayList<>();

      public OrderResponseDTO(List<GroceryCartBook> groceryCartBooks) {

            if (!groceryCartBooks.isEmpty()) {

                  this.id = groceryCartBooks.get(0).getGroceryCart().getId();
                  this.person_id = groceryCartBooks.get(0).getGroceryCart().getPerson().getId();
                  this.status = groceryCartBooks.get(0).getGroceryCart().getOrderStatus().toString();

                  for (GroceryCartBook groceryCartBook : groceryCartBooks) {

                        OrderListBookDTO groceryCartListBookDTO = new OrderListBookDTO(groceryCartBook);
                        this.books.add(groceryCartListBookDTO);
                  }
            }

      }
}
