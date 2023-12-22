package br.com.jmarcos.bookstore.controller.dto.GroceryCartBookDTO;

import java.util.Objects;

import br.com.jmarcos.bookstore.controller.dto.book.BookRequestDTO;
import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroceryCartBookDTO {

      @NotNull(message = "must not be null")
      private Long bookId;

      @NotNull(message = "must not be null")
      @Positive(message = "must be greater than 0")
      private Integer quantity;

      public GroceryCartBook toGroceryCartBook() {
            GroceryCartBook groceryCartBook = new GroceryCartBook();
            groceryCartBook.setBook(BookRequestDTO.toBook(bookId));
            groceryCartBook.setQuantity(this.quantity);

            return groceryCartBook;
      }

      @Override
      public int hashCode() {
            return Objects.hash(bookId);
      }

      @Override
      public boolean equals(Object obj) {
            if (this == obj) {
                  return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                  return false;
            }

            GroceryCartBookDTO otherGroceryCartBookDTO = (GroceryCartBookDTO) obj;
            return Objects.equals(bookId, otherGroceryCartBookDTO.getBookId());

      }
}
