package br.com.jmarcos.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

      Optional<Book> findByTitle(String title);

      List<Book> findAllByAuthorListName(String authorName);

      List<Book> findAllByPublishingCompanyId(Long id);

}
