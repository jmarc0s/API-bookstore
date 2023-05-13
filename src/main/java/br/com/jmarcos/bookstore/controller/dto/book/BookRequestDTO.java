package br.com.jmarcos.bookstore.controller.dto.book;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.jmarcos.bookstore.controller.dto.Storehouse.StorehouseRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.author.AuthorRequestDTO;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class BookRequestDTO {

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
    private Integer year;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Long publishingCompanyId;

    @NotEmpty
    private Set<Long> authorIdList = new HashSet();

    @NotEmpty
    private List<Long> storehouseIdList = new ArrayList<>();
    @NotEmpty
    private List<Integer> quantityInStorehouse = new ArrayList<>();

    private Set<Long> storehouseIdListLinked;

    public Book toBook() {
        Book book = new Book();
        book.setTitle(title);
        book.setYear(year);
        book.setPrice(price);
        PublishingCompany publishingCompany = new PublishingCompany(publishingCompanyId);
        book.setPublishingCompany(publishingCompany);
        for (Long authorId : authorIdList) {
            Author author = AuthorRequestDTO.toAuthor(authorId);
            book.getAuthorList().add(author);
        }

        for (Long storehouseId : storehouseIdListLinked) {
            Storehouse storehouse = StorehouseRequestDTO.toStorehouse(storehouseId);
            book.getStorehouseList().add(storehouse);

        }

        return book;
    }

    public static Book toBook(Long bookId) {
        Book book = new Book();
        book.setId(bookId);
        return book;
    }

    public Boolean verifyCompatibility() {
        this.storehouseIdListLinked = new LinkedHashSet<>(storehouseIdList);
        if (quantityInStorehouse.size() != storehouseIdListLinked.size()) {
            return false;
        }

        return true;
    }
}
