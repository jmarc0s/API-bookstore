package br.com.jmarcos.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

      Optional<Permission> findByName(String name);

}
