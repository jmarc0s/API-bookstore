package br.com.jmarcos.bookstore.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.repository.AuthorRepository;
import jakarta.transaction.Transactional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookService bookService;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookService bookService) {
        this.authorRepository = authorRepository;
        this.bookService = bookService;
    }

    public Page<Author> search(Pageable pageable) {
        return this.authorRepository.findAll(pageable);
    }

    public boolean existsByName(String authorRequestDTOName) {
        Optional<Author> exists = authorRepository.findByName(authorRequestDTOName);

        return exists.isPresent();
    }

    public Author save(Author author) {
        return this.authorRepository.save(author);
    }

    public Optional<Author> searchById(Long id) {
        return this.authorRepository.findById(id);
    }

    public Optional<Author> searchByName(String name) {
        return this.authorRepository.findByName(name);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Optional<Author> exists = this.authorRepository.findById(id);
        if (exists.isPresent()) {

            for (Book book : exists.get().getBookList()) {
                this.bookService.deleteStorehouseBook(book.getId());
            }
            this.authorRepository.deleteById(id);
            return true;
        }

        return false;
    }

    public Optional<Author> update(Author newAuthor) {
        Optional<Author> oldAuthor = this.authorRepository.findById(newAuthor.getId());

        return oldAuthor.isPresent()
                ? Optional.of(this.save(this.fillUpdate(oldAuthor.get(), newAuthor)))
                : Optional.empty();
    }

    private Author fillUpdate(Author oldAuthor, Author newAuthor) {
        newAuthor.getAddress().setId(oldAuthor.getAddress().getId());

        oldAuthor.setName(newAuthor.getName());
        oldAuthor.setUrl(newAuthor.getUrl());
        oldAuthor.setAddress(newAuthor.getAddress());
        return oldAuthor;
    }

}
