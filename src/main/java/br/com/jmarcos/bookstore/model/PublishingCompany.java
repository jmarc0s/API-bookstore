package br.com.jmarcos.bookstore.model;

import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "publishing_company")
public class PublishingCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publishing_company_id")
    private Long id;

    @Column(name = "publishing_company_name")
    private String name;

    @Column(name = "publishing_company_url")
    private String url;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "publishing_company_phone")
    private String phone;

    @OneToMany(mappedBy = "publishingCompany", cascade = CascadeType.REMOVE)
    private List<Book> bookList = new ArrayList<>();

    public PublishingCompany(String requestName, String requestUrl, Address requestAddress, String requestPhone) {
        this.name = requestName;
        this.url = requestUrl;
        this.address = requestAddress;
        this.phone = requestPhone;
    }

    public PublishingCompany(Long id) {
        this.id = id;
    }

}
