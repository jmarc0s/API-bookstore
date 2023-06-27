package br.com.jmarcos.bookstore.controller.dto.storehouse;

import br.com.jmarcos.bookstore.model.Storehouse;
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
public class StorehouseUpdateDTO {

    @NotNull (message = "must not be null")
    @Pattern (regexp = "\\+?\\d{2}\\s?\\d{4,5}[-\\s]?\\d{4}", message = "Invalid phone")
    private String phone;

    public Storehouse toStorehouse(Long id) {
        Storehouse storehouse = new Storehouse();
        storehouse.setId(id);
        storehouse.setPhone(phone);

        return storehouse;
    }
}
