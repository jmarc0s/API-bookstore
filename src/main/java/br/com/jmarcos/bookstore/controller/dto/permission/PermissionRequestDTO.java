package br.com.jmarcos.bookstore.controller.dto.permission;

import br.com.jmarcos.bookstore.model.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDTO {

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String name;

    public Permission toPermission() {
        Permission permission = new Permission();
        permission.setName(name);
        return permission;
    }

    public Permission toPermission(Long id) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setName(this.name);
        
        return permission;
    }
}
