package br.com.jmarcos.bookstore.controller.dto.address;

import br.com.jmarcos.bookstore.model.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDTO {

    private Long id;
    private String street;
    private int number;
    private String city;
    private String state;
    private String zipCode;

    public AddressResponseDTO(Address address) {
        if (address != null) {
            this.id = address.getId();
            this.street = address.getStreet();
            this.number = address.getNumber();
            this.city = address.getCity();
            this.state = address.getState();
            this.zipCode = address.getZipCode();
        }

    }

}
