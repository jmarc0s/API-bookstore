package br.com.jmarcos.bookstore.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.jmarcos.bookstore.model.enums.BookCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "book_id")
        private Long id;

        @Column(name = "book_title")
        private String title;

        @Column(name = "book_year")
        private int year;

        @Column(name = "book_price")
        private BigDecimal price;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "publishingCompany_id")
        private PublishingCompany publishingCompany;

        @Enumerated(EnumType.STRING)
        @Column(name = "book_categories")
        private Set<BookCategory> bookCategories;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "author_book", joinColumns = {
                        @JoinColumn(name = "fk_book")
        }, inverseJoinColumns = {
                        @JoinColumn(name = "fk_author")
        })
        private List<Author> authorList = new ArrayList<>();

        @ManyToMany(mappedBy = "bookList", fetch = FetchType.EAGER)
        private List<Storehouse> storehouseList = new ArrayList<>();

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "grocery_cart_book", joinColumns = {
                        @JoinColumn(name = "book_id")
        }, inverseJoinColumns = {
                        @JoinColumn(name = "grocery_cart_id")
        })
        private List<GroceryCart> groceryCarts = new ArrayList<>();
}
