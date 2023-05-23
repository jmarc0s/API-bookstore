package br.com.jmarcos.bookstore.controller.dto.person;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.jmarcos.bookstore.controller.dto.address.AddressRequestDTO;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.model.Person;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDTO {

    @NotEmpty
    @NotNull
    // @JsonProperty(value = "Person's name")
    private String name;

    @NotEmpty
    @NotNull
    @Email
    private String email;

    @Valid
    @NotNull
    private AddressRequestDTO address;

    @NotEmpty
    @NotNull
    private String phone;

    @NotEmpty
    @NotNull
    private String password;

    public Person toPerson() {
        Person person = new Person();
        person.setName(this.name);
        person.setEmail(email);
        person.setAddress(address.toAddress());
        person.setPhone(phone);
        person.setPassword(new BCryptPasswordEncoder().encode(password));
        person.getPermission().add(new Permission("ROLE_USER"));
        return person;
    }

}
