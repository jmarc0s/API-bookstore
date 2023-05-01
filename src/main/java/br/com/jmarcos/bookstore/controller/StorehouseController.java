package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import br.com.jmarcos.bookstore.controller.dto.Storehouse.StorehouseRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.Storehouse.StorehouseResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.Storehouse.StorehouseUpdateDTO;
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

    @CacheEvict(value = "StorehouseList", allEntries = true)
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid StorehouseRequestDTO storehouseRequestDTO,
            UriComponentsBuilder uriBuilder) {

        if (storehouseService.existsByCode(storehouseRequestDTO.getCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Storehouse code is already in use.");
        }

        Storehouse storehouse = storehouseRequestDTO.toStorehouse();
        this.storehouseService.save(storehouse);
        URI uri = uriBuilder.path("/storehouse/{id}").buildAndExpand(storehouse.getId()).toUri();
        return ResponseEntity.created(uri).body(new StorehouseResponseDTO(storehouse));
    }

    @Cacheable(value = "StorehouseList")
    @GetMapping
    public Page<StorehouseResponseDTO> search(Pageable pageable) {
        return this.storehouseService
                .search(pageable)
                .map(StorehouseResponseDTO::new);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> searchById(@PathVariable Long id) {
        Optional<Storehouse> storehouse = this.storehouseService.searchByID(id);

        return storehouse.isPresent()
                ? ResponseEntity.ok(new StorehouseResponseDTO(storehouse.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "Storehouse Not Found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        boolean removed = this.storehouseService.delete(id);

        return removed ? ResponseEntity.status(HttpStatus.OK).body(
                "storehouse was deleted")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "storehouse Not Found");
    }

    @CacheEvict(value = "StorehouseList", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
            @RequestBody @Valid StorehouseUpdateDTO storehouseUpdateDTO) {
        Optional<Storehouse> storehouse = this.storehouseService.searchByID(id);

        if (storehouse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "Storehouse Not Found");
        }

        storehouse = this.storehouseService.update(storehouseUpdateDTO.toStorehouse(id));
        return storehouse.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "Storehouse Not Found")
                : ResponseEntity.ok(new StorehouseResponseDTO(storehouse.get()));
    }

    @RequestMapping(value = "/search_by_code", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByCode(@RequestParam Integer code) {
        Optional<Storehouse> storehouse = this.storehouseService.searchByCode(code);

        return storehouse.isPresent()
                ? ResponseEntity.ok(new StorehouseResponseDTO(storehouse.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "Storehouse Not Found");
    }

    @RequestMapping(value = "/search_by_address", method = RequestMethod.GET)
    public List<StorehouseResponseDTO> searchByAddress(@RequestParam(required = false) String street,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String zipCode) {
        return this.storehouseService
                .findStorehousesByAddress(street, number, city, state, zipCode)
                .stream()
                .map(StorehouseResponseDTO::new)
                .collect(Collectors.toList());
    }
}
