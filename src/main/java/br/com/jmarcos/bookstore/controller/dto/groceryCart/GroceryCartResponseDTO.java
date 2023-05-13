package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import java.util.ArrayList;
import java.util.List;

import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroceryCartResponseDTO {
    private Long id;
    private Long person_id;
    private List<GroceryCartListBookDTO> books = new ArrayList<>();

    public GroceryCartResponseDTO(GroceryCart groceryCart, List<GroceryCartBook> groceryCartBooks) {
        this.id = groceryCart.getId();
        this.person_id = groceryCart.getPerson().getId();

        for (GroceryCartBook groceryCartBook : groceryCartBooks) {

            GroceryCartListBookDTO groceryCartListBookDTO = new GroceryCartListBookDTO(groceryCartBook);
            this.books.add(groceryCartListBookDTO);
        }

    }
}
