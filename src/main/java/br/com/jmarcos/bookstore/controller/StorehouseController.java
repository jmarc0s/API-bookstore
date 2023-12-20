package br.com.jmarcos.bookstore.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import br.com.jmarcos.bookstore.controller.dto.storehouse.StorehouseRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.storehouse.StorehouseResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.storehouse.StorehouseUpdateDTO;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.service.StorehouseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/storehouses")
public class StorehouseController {

      private final StorehouseService storehouseService;

      @Autowired
      public StorehouseController(StorehouseService storehouseService) {
            this.storehouseService = storehouseService;
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "record a new Storehouse", description = "save a storehouse in database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "409", ref = "conflict")
      })
      // FIXME
      // especificar o tipo de retorno no responseEntity
      @CacheEvict(value = "StorehouseList", allEntries = true)
      @PostMapping
      public ResponseEntity<Object> save(@RequestBody @Valid StorehouseRequestDTO storehouseRequestDTO,
                  UriComponentsBuilder uriBuilder) {

            Storehouse storehouse = this.storehouseService.save(storehouseRequestDTO.toStorehouse());
            URI uri = uriBuilder.path("/storehouse/{id}").buildAndExpand(storehouse.getId()).toUri();
            return ResponseEntity.created(uri).body(new StorehouseResponseDTO(storehouse));
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "Returns a list of Storehouses", description = "Returns a list of all storehouses in database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),

      })
      // FIXME
      // retornar lista ao inves de page
      @Cacheable(value = "StorehouseList")
      @GetMapping
      public Page<StorehouseResponseDTO> search(Pageable pageable) {
            return this.storehouseService
                        .search(pageable)
                        .map(StorehouseResponseDTO::new);
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "Returns a Storehouse by id", description = "returns a storehouse by the specified id", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })

      // FIXME
      // especificar o tipo de retorno no responseEntity
      @GetMapping("/{id}")
      public ResponseEntity<Object> searchById(@PathVariable Long id) {
            Storehouse storehouse = this.storehouseService.searchByID(id);
            return ResponseEntity.ok(new StorehouseResponseDTO(storehouse));
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "delete a storehouse by id", description = "delete a storehouse by the specified id from database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })

      // FIXME
      // especificar o tipo de retorno no responseEntity e retornar um no content
      @DeleteMapping("/{id}")
      public ResponseEntity<Object> delete(@PathVariable Long id) {
            this.storehouseService.delete(id);

            return ResponseEntity.status(HttpStatus.OK).body("storehouse was deleted");
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "updates a storehouse", description = "update data like code, address etc", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })
      // FIXME
      // especificar o tipo de retorno no responseEntity
      @CacheEvict(value = "StorehouseList", allEntries = true)
      @PutMapping("/{id}")
      public ResponseEntity<Object> update(@PathVariable Long id,
                  @RequestBody @Valid StorehouseUpdateDTO storehouseUpdateDTO) {
            Storehouse storehouse = this.storehouseService.searchByID(id);

            storehouse = this.storehouseService.update(storehouseUpdateDTO.toStorehouse(id));

            return ResponseEntity.ok(new StorehouseResponseDTO(storehouse));
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "Returns a Storehouse by code", description = "returns a storehouse by the specified code", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")
      })
      // FIXME
      // especificar o tipo de retorno no responseEntity
      @RequestMapping(value = "/search_by_code", method = RequestMethod.GET)
      public ResponseEntity<Object> searchByCode(@RequestParam Integer code) {
            Storehouse storehouse = this.storehouseService.searchByCode(code);

            return ResponseEntity.ok(new StorehouseResponseDTO(storehouse));
      }

}
