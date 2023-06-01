package br.com.jmarcos.bookstore.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.repository.PermissionRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission searchByname(String name) {
        return this.permissionRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with the given name"));
    }

    public List<Permission> search() {
        return this.permissionRepository.findAll();
    }

    public Permission searchByID(Long id) {
        return this.permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not find with the given id"));
    }

    public void delete(Long id) {
        Permission permission = this.searchByID(id);
        this.permissionRepository.deleteById(permission.getId());

    }

    public Permission update(Permission newPermission) {
        Permission oldPermission = this.searchByID(newPermission.getId());

        if (!Objects.equals(newPermission.getName(), oldPermission.getName())
            && this.existsByname(newPermission.getName())) {

            throw new ConflictException("permission name is already in use");

        }

        oldPermission.setName(newPermission.getName());


        return this.permissionRepository.save(oldPermission);
    }

    public Permission save(Permission permission) {

        if(this.existsByname(permission.getName())){
            throw new ConflictException("permission name is already in use");
        }

        return this.permissionRepository.save(permission);
    }

    public boolean existsByname(String name) {
        Optional<Permission> exists = this.permissionRepository.findByName(name);

        return exists.isPresent();
    }

}
