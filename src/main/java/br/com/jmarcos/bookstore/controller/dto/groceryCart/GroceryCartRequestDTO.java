package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import java.util.HashSet;
import java.util.Set;

import br.com.jmarcos.bookstore.controller.dto.GroceryCartBookDTO.GroceryCartBookDTO;
import br.com.jmarcos.bookstore.controller.dto.book.BookRequestDTO;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Person;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroceryCartRequestDTO {

    @Valid
    @NotEmpty
    private Set<GroceryCartBookDTO> books = new HashSet<>();

    public GroceryCart toGroceryCart(Long personId) {
        GroceryCart groceryCart = new GroceryCart();

        for (GroceryCartBookDTO groceryCartBookDTO : books) {
            Book book = BookRequestDTO.toBook(groceryCartBookDTO.getBookId());
            groceryCart.getBooks().add(book);
        }
        groceryCart.setPerson(new Person(personId));

        return groceryCart;
    }

}
