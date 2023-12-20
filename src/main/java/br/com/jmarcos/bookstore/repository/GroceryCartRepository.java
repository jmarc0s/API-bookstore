package br.com.jmarcos.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.GroceryCart;

@Repository
public interface GroceryCartRepository extends JpaRepository<GroceryCart, Long> {

      List<GroceryCart> findAllByPersonId(Long personId);

      Optional<GroceryCart> findByIdAndPersonId(Long id, Long personId);

      @Query(value = "SELECT gc.* FROM grocery_cart as gc JOIN person as p ON  gc.person_id = p.person_id WHERE gc.order_status ='OPEN' AND p.person_id = :pessoaId", nativeQuery = true)
      Optional<GroceryCart> findOpenOrder(@Param("pessoaId") Long pessoaId);

}
