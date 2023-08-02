package br.com.jmarcos.bookstore.controller.dto.book;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import br.com.jmarcos.bookstore.controller.dto.author.AuthorRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.storehouse.StorehouseRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.storehouseBookDTO.StorehouseBookDTO;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.model.enums.BookCategory;
import jakarta.persistence.ElementCollection;
import jakarta.validation.Valid;
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
public class BookUpdateDTO {

    @NotEmpty
    @NotNull
    private String title;

    @NotNull
    @Positive
    @Digits(integer = 4, fraction = 0)
    private int year;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Long publishingCompanyId;

    @NotNull(message = "must not be null")
    @NotEmpty(message = "must not be empty")
    private Set<BookCategory> bookCategories = new HashSet<>();

    @ElementCollection
    @NotNull
    private Set<Long> authorIdList = new HashSet<>();

    @Valid
    @NotEmpty
    private Set<StorehouseBookDTO> storehouseBookDTOs = new HashSet<>();

    public Book toBook(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setYear(year);
        book.setPrice(price);
        PublishingCompany publishingCompany = new PublishingCompany(publishingCompanyId);
        book.setPublishingCompany(publishingCompany);
        book.setCategories(bookCategories);

        for (Long authorId : authorIdList) {
            Author author = AuthorRequestDTO.toAuthor(authorId);
            book.getAuthorList().add(author);
        }

        for (StorehouseBookDTO storehouseBookDTO : storehouseBookDTOs) {
            Storehouse storehouse = StorehouseRequestDTO.toStorehouse(storehouseBookDTO.getStorehouseId());
            book.getStorehouseList().add(storehouse);

        }

        return book;

    }
}
