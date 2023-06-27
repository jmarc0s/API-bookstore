package br.com.jmarcos.bookstore.controller.dto.person;

import br.com.jmarcos.bookstore.controller.dto.address.AddressUpdateDTO;
import br.com.jmarcos.bookstore.model.Person;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonUpdateDTO {

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String name;

    @NotNull (message = "must not be null")
    @Email  (message = "Invalid email")
    @NotEmpty (message = "must not be empty")
    private String email;

    @Valid
    @NotNull (message = "must not be null")
    private AddressUpdateDTO address;

    @NotNull
    @Pattern (regexp = "\\+?\\d{2}\\s?\\d{4,5}[-\\s]?\\d{4}", message = "Invalid phone")
    private String phone;

    public Person toPerson(Long id) {
        Person person = new Person();

        person.setId(id);
        person.setName(name);
        person.setEmail(email);
        person.setAddress(address.toAddress());
        person.setPhone(phone);
        
        return person;

    }
}
