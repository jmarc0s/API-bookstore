package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.bookstore.controller.dto.permission.PermissionRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.permission.PermissionResponseDTO;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.service.PermissionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
  private final PermissionService permissionService;

  @Autowired
  public PermissionController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @PostMapping
  public ResponseEntity<Object> save(@RequestBody @Valid PermissionRequestDTO permissionRequestDTO,
      UriComponentsBuilder uriBuilder) {
    Optional<Permission> permission = this.permissionService.searchByname(permissionRequestDTO.getName());
    if (permission.isEmpty()) {
      permission = this.permissionService.save(permissionRequestDTO.toPermission());
      URI uri = uriBuilder.path("/permissions/{id}").buildAndExpand(permission.get().getId()).toUri();
      return ResponseEntity.created(uri).body(new PermissionResponseDTO(permission.get()));
    }

    return ResponseEntity.status(HttpStatus.CONFLICT).body("permission name is already in use");
  }

  @GetMapping
  public List<PermissionResponseDTO> search() {
    return this.permissionService
        .search()
        .stream()
        .map(PermissionResponseDTO::new)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> searchById(@PathVariable Long id) {
    Optional<Permission> permission = this.permissionService.searchByID(id);

    return permission.isPresent()
        ? ResponseEntity.ok(new PermissionResponseDTO(permission.get()))
        : ResponseEntity.status(HttpStatus.NOT_FOUND).body("permission not found");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> delete(@PathVariable Long id) {
    boolean removed = this.permissionService.delete(id);

    return removed
        ? ResponseEntity.ok().build()
        : ResponseEntity.status(HttpStatus.NOT_FOUND).body("permission not found");

  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable Long id,
      @RequestBody @Valid PermissionRequestDTO permissionRequestDTO) {
    Optional<Permission> permission = this.permissionService.searchByID(id);
    if (permission.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("permission not found");
    }

    if (!Objects.equals(permissionRequestDTO.getName(), permission.get().getName())
        && this.permissionService.existsByname(permissionRequestDTO.getName())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("permission name is already in use");
    }
    permission = this.permissionService.update(permissionRequestDTO.toPermission(id));

    return ResponseEntity.ok(new PermissionResponseDTO(permission.get()));

  }
}
