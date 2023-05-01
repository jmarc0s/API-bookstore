package br.com.jmarcos.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.GroceryCart;

@Repository
public interface GroceryCartRepository extends JpaRepository<GroceryCart, Long> {

    List<GroceryCart> findAllByPersonId(Long personId);

    Optional<GroceryCart> findByIdAndPersonId(Long id, Long personId);

}
