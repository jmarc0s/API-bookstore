package br.com.jmarcos.bookstore.controller.dto.person;

import java.util.ArrayList;
import java.util.List;

import br.com.jmarcos.bookstore.controller.dto.address.AddressResponseDTO;
import br.com.jmarcos.bookstore.model.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDTO {

    private Long id;
    private String name;
    private String email;
    private AddressResponseDTO address;
    private String phone;

    public PersonResponseDTO(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.email = person.getEmail();
        this.address = new AddressResponseDTO(person.getAddress());
        this.phone = person.getPhone();
    }
}
