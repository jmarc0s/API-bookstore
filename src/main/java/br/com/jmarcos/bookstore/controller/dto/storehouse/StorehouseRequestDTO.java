package br.com.jmarcos.bookstore.controller.dto.storehouse;

import br.com.jmarcos.bookstore.controller.dto.address.AddressRequestDTO;
import br.com.jmarcos.bookstore.model.Storehouse;
import jakarta.validation.Valid;
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
public class StorehouseRequestDTO {

    @NotNull (message = "must not be null")
    private Integer code;

    @Valid
    @NotNull (message = "must not be null")
    private AddressRequestDTO address;

    @NotNull
    @Pattern (regexp = "\\+?\\d{2}\\s?\\d{4,5}[-\\s]?\\d{4}", message = "Invalid phone")
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