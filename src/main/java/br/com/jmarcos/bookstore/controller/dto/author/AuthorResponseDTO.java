package br.com.jmarcos.bookstore.controller.dto.author;

import br.com.jmarcos.bookstore.controller.dto.address.AddressResponseDTO;
import br.com.jmarcos.bookstore.model.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponseDTO {
    private Long id;
    private String name;
    private AddressResponseDTO address;
    private String url;
    // private List<String> books;

    public AuthorResponseDTO(Author author) {
        this.id = author.getId();
        this.name = author.getName();
        this.address = new AddressResponseDTO(author.getAddress());
        this.url = author.getUrl();

    }
}
