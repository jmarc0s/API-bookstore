package br.com.jmarcos.bookstore.model;

import java.util.ArrayList;
import java.util.List;

import br.com.jmarcos.bookstore.model.enums.OrderStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "grocery_cart")
// FIXME
// mudar o nome para Order
public class GroceryCart {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Column(name = "grocery_cart_id")
      private Long id;

      @ManyToMany(mappedBy = "groceryCarts", fetch = FetchType.EAGER)
      private List<Book> books = new ArrayList<>();

      @ManyToOne(fetch = FetchType.EAGER)
      @JoinColumn(name = "person_id")
      private Person person;

      @Enumerated(EnumType.STRING)
      @Column(name = "order_status")
      private OrderStatusEnum orderSTatus;
}
