package br.com.jmarcos.bookstore.controller.dto.book;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.jmarcos.bookstore.controller.dto.author.AuthorRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.storehouse.StorehouseRequestDTO;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.validation.constraints.NotRepeat;
import br.com.jmarcos.bookstore.validation.constraints.SameSize;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SameSize(firstList = "storehouseIdList", secondList = "quantityInStorehouse")
public class BookUpdateDTO {

    private Long id;

    @NotEmpty
    @NotNull
    private String title;

    @NotNull
    @Positive
    @Digits(integer = 4, fraction = 0) /*
                                        * O parâmetro fraction define o número máximo de dígitos permitidos após a
                                        * vírgula decimal. No exemplo dado, o valor é 0, o que significa que o campo
                                        * não permite frações.
                                        */
    private int year;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Long publishingCompanyId;

    @ElementCollection
    @NotNull
    private Set<Long> authorIdList = new HashSet();

    @ElementCollection
    @NotNull
    @NotRepeat
    private List<Long> storehouseIdList = new ArrayList();

    @NotNull
    private List<Integer> quantityInStorehouse = new ArrayList<>();

    public Book toBook(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setYear(year);
        book.setPrice(price);
        PublishingCompany publishingCompany = new PublishingCompany(publishingCompanyId);
        book.setPublishingCompany(publishingCompany);

        for (Long authorId : authorIdList) {
            Author author = AuthorRequestDTO.toAuthor(authorId);
            book.getAuthorList().add(author);
        }

        for (Long storehouseId : storehouseIdList) {
            Storehouse storehouse = StorehouseRequestDTO.toStorehouse(storehouseId);
            book.getStorehouseList().add(storehouse);
        }

        return book;

    }
}
