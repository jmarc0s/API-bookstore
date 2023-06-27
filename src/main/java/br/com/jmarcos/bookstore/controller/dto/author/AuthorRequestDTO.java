package br.com.jmarcos.bookstore.controller.dto.author;

import br.com.jmarcos.bookstore.controller.dto.address.AddressRequestDTO;
import br.com.jmarcos.bookstore.model.Author;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequestDTO {

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String name;

    @Valid
    @NotNull (message = "must not be null")
    private AddressRequestDTO address;

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String url;

    public Author toAuthor() {
        Author author = new Author();
        author.setName(name);
        author.setAddress(address.toAddress());
        author.setUrl(url);

        return author;
    }

    public static Author toAuthor(Long authorId) {
        Author author = new Author();
        author.setId(authorId);
        return author;
    }
}
