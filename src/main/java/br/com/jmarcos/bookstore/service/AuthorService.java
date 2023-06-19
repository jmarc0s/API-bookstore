package br.com.jmarcos.bookstore.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.repository.AuthorRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.StorehouseBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final StorehouseBookRepository storehouseBookRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, StorehouseBookRepository storehouseBookRepository) {
        this.authorRepository = authorRepository;
        this.storehouseBookRepository = storehouseBookRepository;
    }

    public Page<Author> search(Pageable pageable) {
        return this.authorRepository.findAll(pageable);
    }

    public boolean existsByName(String authorRequestDTOName) {
        Optional<Author> exists = authorRepository.findByName(authorRequestDTOName);

        return exists.isPresent();
    }

    public Author save(Author author) {

        if (this.existsByName(author.getName())) {
            throw new ConflictException("Author name is already in use");
        }

        return this.authorRepository.save(author);
    }

    public Author searchById(Long id) {
        return this.authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with the given id"));
    }

    public Author searchByName(String name) {
        return this.authorRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with the given name"));
    }

    @Transactional
    public void deleteById(Long id) {
        Author exists = this.searchById(id);
        

        for (Book book : exists.getBookList()) {
            this.storehouseBookRepository.deleteAllByBookId(book.getId());
        }

        this.authorRepository.deleteById(id);
      
    }

    public Author update(Author newAuthor) {
        Author oldAuthor = this.searchById(newAuthor.getId());

        if (!Objects.equals(oldAuthor.getName(), newAuthor.getName())
            && this.existsByName(newAuthor.getName())){

                throw new ConflictException("Author name is already in use");

            }

        return this.authorRepository.save(this.fillUpdate(oldAuthor, newAuthor));
    }

    private Author fillUpdate(Author oldAuthor, Author newAuthor) {
        newAuthor.getAddress().setId(oldAuthor.getAddress().getId());

        oldAuthor.setName(newAuthor.getName());
        oldAuthor.setUrl(newAuthor.getUrl());
        oldAuthor.setAddress(newAuthor.getAddress());
        return oldAuthor;
    }

}
