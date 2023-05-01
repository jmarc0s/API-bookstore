package br.com.jmarcos.bookstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Optional<Permission> searchByname(String name) {
        return this.permissionRepository.findByName(name);
    }

    public List<Permission> search() {
        return this.permissionRepository.findAll();
    }

    public Optional<Permission> searchByID(Long id) {
        return this.permissionRepository.findById(id);
    }

    public boolean delete(Long id) {
        Optional<Permission> permission = this.permissionRepository.findById(id);
        if (permission.isPresent()) {
            this.permissionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Permission> update(Permission newPermission) {
        Optional<Permission> oldPermission = this.permissionRepository.findById(newPermission.getId());
        oldPermission.get().setName(newPermission.getName());
        return Optional.of(this.permissionRepository.save(oldPermission.get()));
    }

    public Optional<Permission> save(Permission permission) {
        return Optional.of(this.permissionRepository.save(permission));
    }

    public boolean existsByname(String name) {
        Optional<Permission> exists = this.permissionRepository.findByName(name);
        if (exists.isPresent()) {
            return true;
        }
        return false;
    }

}
