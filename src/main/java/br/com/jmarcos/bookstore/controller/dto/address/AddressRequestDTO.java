package br.com.jmarcos.bookstore.controller.dto.address;

import br.com.jmarcos.bookstore.model.Address;
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
public class AddressRequestDTO {
    @NotEmpty
    @NotNull
    private String street;

    @NotNull
    private int number;

    @NotEmpty
    @NotNull
    private String city;

    @NotEmpty
    @NotNull
    private String state;

    @NotEmpty
    @NotNull
    private String zipCode;

    public Address toAddress() {
        return new Address(street, number, city, state, zipCode);
    }
}
