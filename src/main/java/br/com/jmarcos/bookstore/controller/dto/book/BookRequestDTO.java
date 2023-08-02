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
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "must not be blank")
    @NotNull(message = "must not be null")
    private String title;

    @NotNull(message = "must not be null")
    @Positive(message = "must be greater than 0")
    @Digits(integer = 4, fraction = 0, message = "Invalid year")
    private Integer year;

    @NotNull(message = "must not be null")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    private Long publishingCompanyId;

    @NotNull(message = "must not be null")
    @NotEmpty(message = "must not be empty")
    private Set<BookCategory> bookCategories = new HashSet<>();

    @NotNull(message = "must not be null")
    @NotEmpty(message = "must not be empty")
    private Set<Long> authorIdList = new HashSet<>();

    @NotEmpty(message = "must not be empty")
    @NotNull(message = "must not be null")
    private Set<StorehouseBookDTO> storehouseBookDTOs = new HashSet<>();

    public Book toBook() {
        Book book = new Book();

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

    public static Book toBook(Long bookId) {
        Book book = new Book();
        book.setId(bookId);
        return book;
    }

}
