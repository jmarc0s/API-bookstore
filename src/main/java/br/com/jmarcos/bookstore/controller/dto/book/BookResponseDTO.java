package br.com.jmarcos.bookstore.controller.dto.book;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.model.enums.BookCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDTO {
    private Long id;
    private String title;
    private int year;
    private BigDecimal price;
    private BookListPublishingCompanyDTO PublishingCompany;
    private Set<BookCategory> bookCategories = new HashSet<>();
    private List<BookListAuthorDTO> authors = new ArrayList<>();
    private List<BookListStorehouseDTO> storehouses = new ArrayList<>();

    public BookResponseDTO(Book book) {
        this.setId(book.getId());
        this.setTitle(book.getTitle());
        this.setYear(book.getYear());
        this.setPrice(book.getPrice());
        BookListPublishingCompanyDTO bookListPublishingCompanyDTO = new BookListPublishingCompanyDTO(
                book.getPublishingCompany());
        this.PublishingCompany = bookListPublishingCompanyDTO;
        this.setBookCategories(book.getBookCategories());

        for (Author author : book.getAuthorList()) {
            BookListAuthorDTO bookListAuthorDTO = new BookListAuthorDTO(author);
            this.authors.add(bookListAuthorDTO);
        }

        for (Storehouse storehouse : book.getStorehouseList()) {
            BookListStorehouseDTO bookListStorehouseDTO = new BookListStorehouseDTO(storehouse);
            this.storehouses.add(bookListStorehouseDTO);
        }
    }
}
