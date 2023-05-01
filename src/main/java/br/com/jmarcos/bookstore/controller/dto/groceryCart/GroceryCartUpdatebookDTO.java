package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroceryCartUpdatebookDTO {
    @NotNull
    private Long bookId;
    @NotNull
    @Positive
    private int quantity;
}
