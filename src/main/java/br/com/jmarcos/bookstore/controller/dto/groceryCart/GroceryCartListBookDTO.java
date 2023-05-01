package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroceryCartListBookDTO {
    private Long bookId;
    private String bookTitle;
    private int quantity;

    public GroceryCartListBookDTO(GroceryCartBook groceryCartBook) {
        this.bookId = groceryCartBook.getBook().getId();
        this.bookTitle = groceryCartBook.getBook().getTitle();
        this.quantity = groceryCartBook.getQuantity();
    }
}
