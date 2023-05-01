package br.com.jmarcos.bookstore.controller.dto.book;

import java.util.ArrayList;
import java.util.List;

import br.com.jmarcos.bookstore.model.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BookListAuthorDTO {
    private Long authorId;
    private String authorName;

    public BookListAuthorDTO(Author author) {
        this.authorId = author.getId();
        this.authorName = author.getName();
    }
}
