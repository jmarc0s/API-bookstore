package br.com.jmarcos.bookstore.controller.dto.storehouseBookDTO;

import java.util.Objects;

import br.com.jmarcos.bookstore.controller.dto.storehouse.StorehouseRequestDTO;
import br.com.jmarcos.bookstore.model.intermediateClass.StorehouseBook;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorehouseBookDTO {
    
    @NotNull (message = "must not be null")
    private Long storehouseId;

    @NotNull (message = "must not be null")
    @Positive (message = "must be greater than 0")
    private Integer quantity;

    public StorehouseBook toStorehouseBook(){
        StorehouseBook storehouseBook = new StorehouseBook();
        storehouseBook.setStorehouse(StorehouseRequestDTO.toStorehouse(storehouseId));
        storehouseBook.setQuantity(this.quantity);

        return storehouseBook;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storehouseId);
    }
    
    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
        return false;
    }
    StorehouseBookDTO other = (StorehouseBookDTO) obj;
    return Objects.equals(storehouseId, other.getStorehouseId());
    }


}
