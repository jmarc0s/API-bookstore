package br.com.jmarcos.bookstore.controller.dto.publishinCompany;

import br.com.jmarcos.bookstore.controller.dto.address.AddressUpdateDTO;
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
@AllArgsConstructor
@NoArgsConstructor
public class PublishingCompanyUpdateDTO {
    @NotEmpty
    @NotNull
    private String name;
    @NotEmpty
    @NotNull
    private String url;
    @Valid
    @NotNull
    private AddressUpdateDTO address;
    @NotEmpty
    @NotNull
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
