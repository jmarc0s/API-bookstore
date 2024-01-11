package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddBookDTO {

      @NotNull(message = "must not be null")
      private Long bookId;

      @NotNull(message = "must not be null")
      @Positive(message = "must be greater than 0")
      private int quantity;
}
