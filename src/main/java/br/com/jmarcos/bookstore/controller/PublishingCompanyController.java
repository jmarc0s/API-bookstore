package br.com.jmarcos.bookstore.controller;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

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

import br.com.jmarcos.bookstore.controller.dto.publishinCompany.PublishingCompanyRequestDTO;
import br.com.jmarcos.bookstore.controller.dto.publishinCompany.PublishingCompanyResponseDTO;
import br.com.jmarcos.bookstore.controller.dto.publishinCompany.PublishingCompanyUpdateDTO;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.service.PublishingCompanyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/publishingCompanies")
public class PublishingCompanyController {
    private final PublishingCompanyService publishingCompanyService;

    @Autowired
    public PublishingCompanyController(PublishingCompanyService publishingCompanyService) {
        this.publishingCompanyService = publishingCompanyService;
    }

    @Cacheable(value = "PublishingCompanyList")
    @GetMapping
    public Page<PublishingCompanyResponseDTO> search(Pageable pageable) {
        return this.publishingCompanyService
                .search(pageable)
                .map(PublishingCompanyResponseDTO::new);
    }

    @CacheEvict(value = "PublishingCompanyList", allEntries = true)
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid PublishingCompanyRequestDTO publishingCompanyRequestDTO,
            UriComponentsBuilder uriBuilder) {

        if (publishingCompanyService.existsByName(publishingCompanyRequestDTO.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("publishingCompany name is already in use.");
        }

        PublishingCompany publishingCompany = publishingCompanyRequestDTO.toPublishingCompany();
        this.publishingCompanyService.save(publishingCompany);
        URI uri = uriBuilder.path("/publishingCompany/{id}").buildAndExpand(publishingCompany.getId()).toUri();
        return ResponseEntity.created(uri).body(new PublishingCompanyResponseDTO(publishingCompany));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> searchById(@PathVariable Long id) {
        Optional<PublishingCompany> publishingCompany = this.publishingCompanyService.searchById(id);

        return publishingCompany.isPresent()
                ? ResponseEntity.ok(new PublishingCompanyResponseDTO(publishingCompany.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "PublishingCompany Not Found");
    }

    @RequestMapping(value = "/search_by_name", method = RequestMethod.GET)
    public ResponseEntity<Object> searchByname(@RequestParam String name) {
        Optional<PublishingCompany> publishingCompany = this.publishingCompanyService.searchByName(name);

        return publishingCompany.isPresent()
                ? ResponseEntity.ok(new PublishingCompanyResponseDTO(publishingCompany.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "PublishingCompany Not Found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        boolean removed = this.publishingCompanyService.delete(id);

        return removed ? ResponseEntity.status(HttpStatus.OK).body(
                "publishingCompany was deleted")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "storehouse Not Found");
    }

    @CacheEvict(value = "PublishingCompanyList", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody @Valid PublishingCompanyUpdateDTO publishingCompanyUpdateDTO,
            @PathVariable Long id) {
        Optional<PublishingCompany> publishingCompany = this.publishingCompanyService.searchById(id);

        if (publishingCompany.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PublishingCompany not found");
        }

        if (!Objects.equals(publishingCompany.get().getName(), publishingCompanyUpdateDTO.getName())
                && publishingCompanyService.existsByName(publishingCompanyUpdateDTO.getName())) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("PublishingCompany name is already in use");
        }

        Optional<PublishingCompany> updattedPublishimgCompany = this.publishingCompanyService
                .update(publishingCompanyUpdateDTO.toPublishingCompany(id), id);

        return updattedPublishimgCompany.isPresent()
                ? ResponseEntity.ok(new PublishingCompanyResponseDTO(updattedPublishimgCompany.get()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("PublishingCompany not found");

    }

}
