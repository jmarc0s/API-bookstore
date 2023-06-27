package br.com.jmarcos.bookstore.controller.dto.publishinCompany;

import br.com.jmarcos.bookstore.controller.dto.address.AddressRequestDTO;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublishingCompanyRequestDTO {

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String name;

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String url;

    @Valid
    @NotNull (message = "must not be null")
    private AddressRequestDTO address;

    @NotNull
    @Pattern (regexp = "\\+?\\d{2}\\s?\\d{4,5}[-\\s]?\\d{4}", message = "Invalid phone")
    private String phone;

    public PublishingCompany toPublishingCompany() {
        return new PublishingCompany(name, url, address.toAddress(), phone);
    }
}
