package br.com.jmarcos.bookstore.controller.dto.person;

import br.com.jmarcos.bookstore.controller.dto.address.AddressUpdateDTO;
import br.com.jmarcos.bookstore.model.Person;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonUpdateDTO {

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String email;

    @Valid
    @NotNull
    private AddressUpdateDTO address;

    @NotEmpty
    @NotNull
    private String phone;

    public Person toPerson() {
        return new Person(name, email, address.toAddress(), phone);

    }
}
