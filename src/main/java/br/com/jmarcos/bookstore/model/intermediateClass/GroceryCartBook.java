package br.com.jmarcos.bookstore.model.intermediateClass;

import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.GroceryCart;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// FIXME
// mudar o nome para OrderBook
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grocery_cart_book")
public class GroceryCartBook {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      private int quantity;

      @ManyToOne(fetch = FetchType.EAGER)
      @JoinColumn(name = "grocery_cart_id")
      private GroceryCart groceryCart;

      @ManyToOne(fetch = FetchType.EAGER)
      @JoinColumn(name = "book_id")
      private Book book;
}
