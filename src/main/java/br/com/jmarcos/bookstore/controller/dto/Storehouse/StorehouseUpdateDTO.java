package br.com.jmarcos.bookstore.controller.dto.Storehouse;

import br.com.jmarcos.bookstore.model.Storehouse;
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
public class StorehouseUpdateDTO {
    @NotEmpty
    @NotNull
    private String phone;

    public Storehouse toStorehouse(Long id) {
        Storehouse storehouse = new Storehouse();
        storehouse.setId(id);
        storehouse.setPhone(phone);

        return storehouse;
    }
}
