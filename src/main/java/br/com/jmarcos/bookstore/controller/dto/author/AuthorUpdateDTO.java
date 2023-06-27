package br.com.jmarcos.bookstore.controller.dto.author;

import br.com.jmarcos.bookstore.controller.dto.address.AddressUpdateDTO;
import br.com.jmarcos.bookstore.model.Author;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class AuthorUpdateDTO {

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String name;

    @Valid
    @NotNull (message = "must not be null")
    private AddressUpdateDTO address;

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String url;

    public Author toAuthor(Long id) {
        Author author = new Author();
        author.setId(id);
        author.setName(name);
        author.setAddress(address.toAddress());
        author.setUrl(url);

        return author;
    }
}
