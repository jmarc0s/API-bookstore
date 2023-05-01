package br.com.jmarcos.bookstore.controller.dto.Storehouse;

import br.com.jmarcos.bookstore.controller.dto.address.AddressRequestDTO;
import br.com.jmarcos.bookstore.model.Storehouse;
import jakarta.validation.Valid;
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
public class StorehouseRequestDTO {

    @NotNull
    private Integer code;
    @Valid
    @NotNull
    private AddressRequestDTO address;
    @NotNull
    @NotEmpty
    private String phone;

    public Storehouse toStorehouse() {
        return new Storehouse(code, address.toAddress(), phone);
    }

    public static Storehouse toStorehouse(Long storehouseId) {
        Storehouse storehouse = new Storehouse();
        storehouse.setId(storehouseId);
        return storehouse;
    }
}