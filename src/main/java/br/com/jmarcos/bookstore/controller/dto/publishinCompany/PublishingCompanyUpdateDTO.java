package br.com.jmarcos.bookstore.controller.dto.publishinCompany;

import br.com.jmarcos.bookstore.controller.dto.address.AddressUpdateDTO;
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
@AllArgsConstructor
@NoArgsConstructor
public class PublishingCompanyUpdateDTO {

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String name;

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String url;

    @Valid
    @NotNull (message = "must not be null")
    private AddressUpdateDTO address;

    @NotNull
    @Pattern (regexp = "\\+?\\d{2}\\s?\\d{4,5}[-\\s]?\\d{4}", message = "Invalid phone")
    private String phone;

    public PublishingCompany toPublishingCompany(Long id) {
        PublishingCompany publishingCompany = new PublishingCompany();
        
        publishingCompany.setName(name);
        publishingCompany.setUrl(url);
        publishingCompany.setAddress(address.toAddress());
        publishingCompany.setPhone(phone);

        return publishingCompany;
    }

}
