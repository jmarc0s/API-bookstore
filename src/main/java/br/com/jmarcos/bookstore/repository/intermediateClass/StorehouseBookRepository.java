package br.com.jmarcos.bookstore.repository.intermediateClass;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.intermediateClass.StorehouseBook;

@Repository
public interface StorehouseBookRepository extends JpaRepository<StorehouseBook, Long> {

    void deleteAllByBookId(Long id);

    List<StorehouseBook> findAllByBookId(Long id);

}
