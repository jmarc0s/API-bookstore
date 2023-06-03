package br.com.jmarcos.bookstore.controller.dto.groceryCart;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.jmarcos.bookstore.controller.dto.book.BookRequestDTO;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.validation.constraints.NotRepeat;
import br.com.jmarcos.bookstore.validation.constraints.SameSize;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SameSize(firstList = "bookIdListArray", secondList = "quantities")
public class GroceryCartRequestDTO {
    @NotEmpty
    @NotRepeat
    private List<Long> bookIdListArray = new ArrayList<>();
    @NotEmpty
    private List<Integer> quantities = new ArrayList<>();
    private Set<Long> bookIdListlinked;

    public GroceryCart toGroceryCart(Long personId) {
        GroceryCart groceryCart = new GroceryCart();

        for (Long bookId : bookIdListArray) {
            Book book = BookRequestDTO.toBook(bookId);
            groceryCart.getBooks().add(book);
        }
        groceryCart.setPerson(new Person(personId));

        return groceryCart;
    }

    public Boolean verifyCompatibility() {
        this.bookIdListlinked = new LinkedHashSet<>(bookIdListArray);
        if (quantities.size() != bookIdListlinked.size()) {
            return false;
        }

        return true;
    }
}
