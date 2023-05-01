package br.com.jmarcos.bookstore.controller.dto.publishinCompany;

import br.com.jmarcos.bookstore.controller.dto.address.AddressResponseDTO;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublishingCompanyResponseDTO {
    private Long id;
    private String name;
    private String url;
    private AddressResponseDTO address;
    private String phone;

    public PublishingCompanyResponseDTO(PublishingCompany publishingCompany) {
        this.id = publishingCompany.getId();
        this.name = publishingCompany.getName();
        this.url = publishingCompany.getUrl();
        this.address = new AddressResponseDTO(publishingCompany.getAddress());
        this.phone = publishingCompany.getPhone();
    }
}
