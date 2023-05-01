package br.com.jmarcos.bookstore.controller.dto.book;

import br.com.jmarcos.bookstore.model.Storehouse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookListStorehouseDTO {
    private Long storehouseId;
    private Integer storehouseCode;

    public BookListStorehouseDTO(Storehouse storehouse) {
        this.storehouseId = storehouse.getId();
        this.storehouseCode = storehouse.getCode();
    }
}
