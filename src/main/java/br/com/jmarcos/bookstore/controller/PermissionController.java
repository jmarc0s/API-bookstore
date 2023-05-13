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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "record a new book", description = "save a new book in database", responses = {
      @ApiResponse(responseCode = "500", ref = "InternalServerError"),
      @ApiResponse(responseCode = "200", description = "Permission saved", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(value = "{"
              + "\"id\": 123,"
              + "\"name\": \"ADMIN\","
              + "}")
      })),
      @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
      @ApiResponse(responseCode = "403", ref = "permissionDenied"),
      @ApiResponse(responseCode = "404", description = "permision not found in database"),
      @ApiResponse(responseCode = "409", description = "permission name is already in use by other permission in database")
  })

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

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "Returns a list of permissions", description = "Returns a list of all permissions in database", responses = {
      @ApiResponse(responseCode = "500", ref = "InternalServerError"),
      @ApiResponse(responseCode = "200", ref = "ok"),
      @ApiResponse(responseCode = "403", ref = "permissionDenied"),

  })

  @GetMapping
  public List<PermissionResponseDTO> search() {
    return this.permissionService
        .search()
        .stream()
        .map(PermissionResponseDTO::new)
        .collect(Collectors.toList());
  }

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "returns a permission by id", description = "returns permission by the specified id", responses = {
      @ApiResponse(responseCode = "500", ref = "InternalServerError"),
      @ApiResponse(responseCode = "200", description = "Successful request", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(value = "{"
              + "\"id\": 123,"
              + "\"name\": \"ADMIN\","
              + "}")
      })),
      @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
      @ApiResponse(responseCode = "403", ref = "permissionDenied"),
      @ApiResponse(responseCode = "404", description = "permision not found in database")
  })

  @GetMapping("/{id}")
  public ResponseEntity<Object> searchById(@PathVariable Long id) {
    Optional<Permission> permission = this.permissionService.searchByID(id);

    return permission.isPresent()
        ? ResponseEntity.ok(new PermissionResponseDTO(permission.get()))
        : ResponseEntity.status(HttpStatus.NOT_FOUND).body("permission not found");
  }

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "delete a permision by id", description = "delete a permision by the specified id from database", responses = {
      @ApiResponse(responseCode = "500", ref = "InternalServerError"),
      @ApiResponse(responseCode = "200", ref = "ok"),
      @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
      @ApiResponse(responseCode = "403", ref = "permissionDenied"),
      @ApiResponse(responseCode = "404", description = "permission not found in database")
  })

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> delete(@PathVariable Long id) {
    boolean removed = this.permissionService.delete(id);

    return removed
        ? ResponseEntity.ok().build()
        : ResponseEntity.status(HttpStatus.NOT_FOUND).body("permission not found");

  }

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "update a book", description = "update a permission name", responses = {
      @ApiResponse(responseCode = "500", ref = "InternalServerError"),
      @ApiResponse(responseCode = "200", description = "permission updated", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(value = "{"
              + "\"id\": 123,"
              + "\"name\": \"ADMIN\","
              + "}")
      })),
      @ApiResponse(responseCode = "400", description = "bad request, you may have filled something wrong"),
      @ApiResponse(responseCode = "403", ref = "permissionDenied"),
      @ApiResponse(responseCode = "404", description = "permision not found in database"),
      @ApiResponse(responseCode = "409", description = "permission name is already in use by other permission in database")
  })

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
