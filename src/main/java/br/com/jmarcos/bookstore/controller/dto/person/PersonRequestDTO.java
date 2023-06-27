package br.com.jmarcos.bookstore.controller.dto.person;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.jmarcos.bookstore.controller.dto.address.AddressRequestDTO;
import br.com.jmarcos.bookstore.model.Permission;
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
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDTO {

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    // @JsonProperty(value = "Person's name")
    private String name;

    @NotNull (message = "must not be null")
    @Email  (message = "Invalid email")
    @NotEmpty (message = "must not be empty")
    private String email;

    @Valid
    @NotNull (message = "must not be null")
    private AddressRequestDTO address;

    @NotNull
    @Pattern (regexp = "\\+?\\d{2}\\s?\\d{4,5}[-\\s]?\\d{4}", message = "Invalid phone")
    private String phone;

    @NotEmpty (message = "must not be empty")
    @NotNull (message = "must not be null")
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
