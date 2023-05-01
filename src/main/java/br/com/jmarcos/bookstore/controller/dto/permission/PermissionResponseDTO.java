package br.com.jmarcos.bookstore.controller.dto.permission;

import br.com.jmarcos.bookstore.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {
    private Long id;
    private String name;

    public PermissionResponseDTO(Permission permission) {
        this.id = permission.getId();
        this.name = permission.getName();
    }
}
