package br.com.jmarcos.bookstore.repository.intermediateClass;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import br.com.jmarcos.bookstore.service.GroceryCartService;

@Repository
public interface GroceryCartBookRepository extends JpaRepository<GroceryCartBook, Long> {

    void deleteAllByGroceryCartId(Long id);

    void deleteByGroceryCartIdAndBookId(Long id, Long id2);

    Optional<GroceryCartBook> findByGroceryCartIdAndBookId(Long id, Long id2);

    List<GroceryCartBook> findAllByGroceryCartId(Long id);

}
