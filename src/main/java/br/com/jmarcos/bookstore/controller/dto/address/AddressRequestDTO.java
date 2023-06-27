package br.com.jmarcos.bookstore.controller.dto.address;

import br.com.jmarcos.bookstore.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {
    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String street;

    @NotNull (message = "must not be null")
    @Positive (message = "must be greater than 0")
    private int number;

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String city;

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String state;

    @NotNull (message = "must not be null")
    @Pattern(regexp = "\\d{5}[-\\s]\\d{3}", message = "Invalid zip code")
    private String zipCode;

    public Address toAddress() {
        return new Address(street, number, city, state, zipCode);
    }
}
