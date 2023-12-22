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
      private List<GroceryCartListBookDTO> books = new ArrayList<>();

      public OrderResponseDTO(List<GroceryCartBook> groceryCartBooks) {

            if (!groceryCartBooks.isEmpty()) {
                  this.id = groceryCartBooks.get(0).getGroceryCart().getId();
                  this.person_id = groceryCartBooks.get(0).getGroceryCart().getPerson().getId();

                  for (GroceryCartBook groceryCartBook : groceryCartBooks) {

                        GroceryCartListBookDTO groceryCartListBookDTO = new GroceryCartListBookDTO(groceryCartBook);
                        this.books.add(groceryCartListBookDTO);
                  }
            }

      }
}
