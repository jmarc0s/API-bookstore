package br.com.jmarcos.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByName(String name);

}
