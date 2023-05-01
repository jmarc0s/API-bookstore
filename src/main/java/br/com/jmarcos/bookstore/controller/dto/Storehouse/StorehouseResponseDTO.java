package br.com.jmarcos.bookstore.controller.dto.Storehouse;

import br.com.jmarcos.bookstore.controller.dto.address.AddressResponseDTO;
import br.com.jmarcos.bookstore.model.Storehouse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorehouseResponseDTO {
    private Long id;
    private Integer code;
    private AddressResponseDTO address;
    private String phone;

    public StorehouseResponseDTO(Storehouse storehouse) {
        this.id = storehouse.getId();
        this.code = storehouse.getCode();
        setAddress(new AddressResponseDTO(storehouse.getAddress()));
        this.phone = storehouse.getPhone();
    }
}
