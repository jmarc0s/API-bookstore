package br.com.jmarcos.bookstore.controller.dto.publishinCompany;

import br.com.jmarcos.bookstore.controller.dto.address.AddressRequestDTO;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublishingCompanyRequestDTO {

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String url;
    @Valid
    @NotNull
    private AddressRequestDTO address;

    @NotEmpty
    @NotNull
    private String phone;

    public PublishingCompany toPublishingCompany() {
        return new PublishingCompany(name, url, address.toAddress(), phone);
    }
}
