package br.com.jmarcos.bookstore.controller.dto.book;

import br.com.jmarcos.bookstore.model.PublishingCompany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookListPublishingCompanyDTO {
    private Long publishingCompanyId;
    private String publishingCompanyName;

    public BookListPublishingCompanyDTO(PublishingCompany publishingCompany) {
        this.publishingCompanyId = publishingCompany.getId();
        this.publishingCompanyName = publishingCompany.getName();
    }
}
