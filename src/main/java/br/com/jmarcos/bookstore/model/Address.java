package br.com.jmarcos.bookstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Column(name = "address_street")
    private String street;

    @Column(name = "address_number")
    private int number;

    @Column(name = "address_city")
    private String city;

    @Column(name = "address_state")
    private String state;

    @Column(name = "address_zip_code")
    private String zipCode;

    public Address(String street2, int number2, String city2, String state2, String zipCode2) {
        this.street = street2;
        this.number = number2;
        this.city = city2;
        this.state = state2;
        this.zipCode = zipCode2;
    }
}
