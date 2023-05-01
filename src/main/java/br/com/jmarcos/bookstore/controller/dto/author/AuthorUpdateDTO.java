package br.com.jmarcos.bookstore.controller.dto.author;

import br.com.jmarcos.bookstore.controller.dto.address.AddressUpdateDTO;
import br.com.jmarcos.bookstore.model.Author;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    @NotNull
    private String name;

    @Valid
    @NotNull
    private AddressUpdateDTO address;

    @NotEmpty
    @NotNull
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
