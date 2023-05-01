package br.com.jmarcos.bookstore.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "storehouse")
public class Storehouse {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "storehouse_id")
        private Long id;

        @Column(name = "storehouse_code")
        private Integer code;

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "address_id")
        private Address address;

        @Column(name = "storehouse_phone")
        private String phone;

        @ManyToMany
        @JoinTable(name = "storehouse_book", joinColumns = {
                        @JoinColumn(name = "storehouse_id")
        }, inverseJoinColumns = {
                        @JoinColumn(name = "book_id")
        })
        private List<Book> bookList = new ArrayList<>();

        public Storehouse(Integer codeRequest, Address addressRequest, String phoneRequest) {
                this.code = codeRequest;
                this.address = addressRequest;
                this.phone = phoneRequest;
        }

}
