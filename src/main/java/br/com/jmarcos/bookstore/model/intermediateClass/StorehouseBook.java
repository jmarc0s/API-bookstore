package br.com.jmarcos.bookstore.model.intermediateClass;

import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.Storehouse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "storehouse_book")
public class StorehouseBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "storehouse_id")
    private Storehouse storehouse;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

}
