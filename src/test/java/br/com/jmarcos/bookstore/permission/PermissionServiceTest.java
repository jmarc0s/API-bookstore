package br.com.jmarcos.bookstore.permission;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jmarcos.bookstore.controller.dto.permission.PermissionRequestDTO;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.repository.PermissionRepository;
import br.com.jmarcos.bookstore.service.PermissionService;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;

    @Mock
    private PermissionRepository permissionRepository;

    @Test
    void searchByName_returns_APermissionheGivenName_WhenSuccessful() {
        Permission permission = createPermission();
        when(permissionRepository.findByName(anyString())).thenReturn(Optional.of(permission));

        Permission returnedPermission = this.permissionService.searchByName(permission.getName());

        Assertions.assertNotNull(returnedPermission);
        Assertions.assertEquals(permission.getId(), returnedPermission.getId());
        Assertions.assertEquals(permission.getName(), returnedPermission.getName());
        verify(permissionRepository).findByName(permission.getName());
    }

    @Test
    void searchByName_Throws_ResourceNotFoundException_WhenPermissionNotFound() {
        when(permissionRepository.findByName(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> permissionService.searchByName(anyString()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Permission not found with the given name"));
        
    }

    @Test
    void search_returns_AllPermissions_WhenSuccessful() {   
        List<Permission> permissionList = List.of(createPermission());
        when(permissionRepository.findAll()).thenReturn(permissionList);

        List<Permission> all = permissionService.search();

        Assertions.assertFalse(all.isEmpty());
        Assertions.assertNotNull(all.get(0).getId());
         Assertions.assertEquals(permissionList.get(0).getName(), all.get(0).getName());

        verify(permissionRepository).findAll();

    }

    @Test
    void searchByID_returns_APermissionheGivenId_WhenSuccessful() {
        Permission permission = createPermission();
        when(permissionRepository.findById(permission.getId())).thenReturn(Optional.of(permission));

        Permission returnedPermission = this.permissionService.searchById(permission.getId());

        Assertions.assertNotNull(returnedPermission);
        Assertions.assertEquals(permission.getId(), returnedPermission.getId());
        Assertions.assertEquals(permission.getName(), returnedPermission.getName());
        verify(permissionRepository).findById(permission.getId());
    }

    @Test
    void searchByID_Throws_ResourceNotFoundException_WhenPermissionNotFound() {
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> permissionService.searchById(1L));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Permission not find with the given id"));
        
    }

    @Test
    void delete_deletesAStorehouse_WhenSuccessful() {
        Permission permission = createPermission();
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(permission));

        this.permissionService.delete(permission.getId());

        verify(permissionRepository).deleteById(permission.getId());
        verify(permissionRepository).findById(permission.getId());
    }

    @Test
    void delete_Throws_ResourceNotFoundException_WhenPublishingCompanyNotFound() {
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> permissionService.delete(anyLong()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Permission not find with the given id"));
        
    }


    @Test
    void update_returns_AUpdatedPermission_WhenSuccessful() {
        Permission permission = createPermission();
        PermissionRequestDTO permissionRequestDTO = createUpdatePermission();
        when(permissionRepository.save(permission)).thenReturn(permission);
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(permission));

        Permission updatedPermission = permissionService.update(permissionRequestDTO.toPermission(1L));

        Assertions.assertNotNull(updatedPermission);
        Assertions.assertEquals(permission.getId(), updatedPermission.getId());
        Assertions.assertEquals(permissionRequestDTO.getName(), updatedPermission.getName());
        verify(permissionRepository).save(permission);

    }

    @Test
    void update_Throws_ConflictException_WhenPermissionNameIsAlreadyInUse() {
        Permission permission = createPermission();
        PermissionRequestDTO permissionUpdateDTO = createUpdatePermission();
        when(permissionRepository.findById(anyLong())).thenReturn(Optional.of(permission));
        when(permissionRepository.findByName(anyString())).thenReturn(Optional.of(permission));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> permissionService.update(permissionUpdateDTO.toPermission(1L)));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("permission name is already in use"));

    }

    @Test
    void save_returns_ASavedPermission_WhenSuccessful() {
        Permission permission = createPermission();
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        Permission savedPermission = permissionService.save(permission);

        Assertions.assertNotNull(savedPermission);
        Assertions.assertNotNull(savedPermission.getId());
        Assertions.assertEquals(permission.getName(), savedPermission.getName());
        verify(permissionRepository).save(permission);

    }

    @Test
    void save_Throws_ConflictException_WhenPermissionNameIsAlreadyInUse() {
        Permission permission = createPermission();
        Permission newPermission = createPermission();
        when(permissionRepository.findByName(anyString())).thenReturn(Optional.of(permission));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> permissionService.save(newPermission));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("permission name is already in use"));

    }



    Permission createPermission() {
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("ADMIN");
        return permission;
    }

    PermissionRequestDTO createUpdatePermission() {
        PermissionRequestDTO permission = new PermissionRequestDTO();

        permission.setName("USER");

        return permission;
    }

}
