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

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "Returns a list of Publishing Companies", description = "Returns a list of all Publishing Company in database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),

      })

      @Cacheable(value = "PublishingCompanyList")
      @GetMapping
      public Page<PublishingCompanyResponseDTO> search(Pageable pageable) {
            return this.publishingCompanyService
                        .search(pageable)
                        .map(PublishingCompanyResponseDTO::new);
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "record a new publishing Company", description = "save a new Publishing Company in database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "409", ref = "conflict")
      })
      // FIXME
      // especificar o tipo de retorno no responseEntity
      @CacheEvict(value = "PublishingCompanyList", allEntries = true)
      @PostMapping
      public ResponseEntity<Object> save(@RequestBody @Valid PublishingCompanyRequestDTO publishingCompanyRequestDTO,
                  UriComponentsBuilder uriBuilder) {

            PublishingCompany publishingCompany = publishingCompanyRequestDTO.toPublishingCompany();
            this.publishingCompanyService.save(publishingCompany);

            URI uri = uriBuilder.path("/publishingCompany/{id}").buildAndExpand(publishingCompany.getId()).toUri();
            return ResponseEntity.created(uri).body(new PublishingCompanyResponseDTO(publishingCompany));
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "Returns a publishing Company by id", description = "returns a publishing company by the specified id", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")

      })
      // FIXME
      // especificar o tipo de retorno no responseEntity
      @GetMapping("/{id}")
      public ResponseEntity<Object> searchById(@PathVariable Long id) {
            PublishingCompany publishingCompany = this.publishingCompanyService.searchById(id);

            return ResponseEntity.ok(new PublishingCompanyResponseDTO(publishingCompany));
      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "Returns a publishing Company by name", description = "returns a publishing company by the specified name", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")

      })
      // FIXME
      // especificar o tipo de retorno no responseEntity
      @RequestMapping(value = "/search_by_name", method = RequestMethod.GET)
      public ResponseEntity<Object> searchByname(@RequestParam String name) {
            PublishingCompany publishingCompany = this.publishingCompanyService.searchByName(name);

            return ResponseEntity.ok(new PublishingCompanyResponseDTO(publishingCompany));

      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "delete a publishing Company by id", description = "delete a publishing company by the specified id from database", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound")

      })
      // FIXME
      // especificar o tipo de retorno no responseEntity e retornar um no content
      @DeleteMapping("/{id}")
      public ResponseEntity<Object> delete(@PathVariable Long id) {
            this.publishingCompanyService.delete(id);

            return ResponseEntity.status(HttpStatus.OK).body("publishingCompany was deleted");

      }

      @SecurityRequirement(name = "Authorization")
      @Operation(summary = "updates a publishing", description = "update data like, name, url, address etc", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "403", ref = "permissionDenied"),
                  @ApiResponse(responseCode = "404", ref = "ResourceNotFound"),
                  @ApiResponse(responseCode = "409", ref = "conflict")

      })
      // FIXME
      // especificar o tipo de retorno no responseEntity
      @CacheEvict(value = "PublishingCompanyList", allEntries = true)
      @PutMapping("/{id}")
      public ResponseEntity<Object> update(@RequestBody @Valid PublishingCompanyUpdateDTO publishingCompanyUpdateDTO,
                  @PathVariable Long id) {

            PublishingCompany updattedPublishimgCompany = this.publishingCompanyService
                        .update(publishingCompanyUpdateDTO.toPublishingCompany(id), id);

            return ResponseEntity.ok(new PublishingCompanyResponseDTO(updattedPublishimgCompany));

      }

}
