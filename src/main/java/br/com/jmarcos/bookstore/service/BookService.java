package br.com.jmarcos.bookstore.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.model.enums.BookCategory;
import br.com.jmarcos.bookstore.model.intermediateClass.StorehouseBook;
import br.com.jmarcos.bookstore.repository.BookRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.StorehouseBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import br.com.jmarcos.bookstore.specifications.BookSpecification;
import jakarta.transaction.Transactional;

@Service
public class BookService {
      private final BookRepository bookRepository;
      private final PublishingCompanyService publishingCompanyService;
      private final AuthorService authorService;
      private final StorehouseService storehouseService;
      private final StorehouseBookRepository storehouseBookRepository;

      // FIXME
      // retirar anotação Autowired
      @Autowired
      public BookService(BookRepository bookRepository,
                  PublishingCompanyService publishingCompanyService,
                  AuthorService authorService,
                  StorehouseService storehouseService,
                  StorehouseBookRepository storehouseBookRepository) {

            this.bookRepository = bookRepository;
            this.publishingCompanyService = publishingCompanyService;
            this.authorService = authorService;
            this.storehouseService = storehouseService;
            this.storehouseBookRepository = storehouseBookRepository;
      }

      // FIXME
      // substituir esse metodo para o existsByTitle do JpaReporitory
      public boolean existsByTitle(String title) {
            Optional<Book> exists = this.bookRepository.findByTitle(title);

            return exists.isPresent();
      }

      public Page<Book> search(Pageable pageable, Integer year, BigDecimal price, List<BookCategory> categories) {
            return this.bookRepository.findAll(Specification
                        .where(BookSpecification.bookHasPriceLessThan(price))
                        .and(BookSpecification.bookHasYear(year))
                        .and(BookSpecification.bookHasCategories(categories)), pageable);
      }

      public Book findByTitle(String title) {
            return this.bookRepository.findByTitle(title)
                        .orElseThrow(
                                    () -> new ResourceNotFoundException(
                                                "Book not found in database with the specified title"));
      }

      public List<Book> findByAuthorName(String authorName) {
            return this.bookRepository.findAllByAuthorListName(authorName);
      }

      public Book save(Book book, List<StorehouseBook> storehouseBooks) {
            if (this.existsByTitle(book.getTitle())) {
                  throw new ConflictException("Book title is already in use");
            }

            book = this.prepareBook(book, storehouseBooks);

            book = this.bookRepository.save(book);
            this.createStorehouseBook(book, storehouseBooks);

            return book;
      }

      public Book findById(Long id) {
            return this.bookRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                    "Book not found in database with the specified id"));
      }

      @Transactional
      public void deleteById(Long id) {
            Book book = this.findById(id);

            this.storehouseBookRepository.deleteAllByBookId(book.getId());
            this.bookRepository.deleteById(book.getId());
      }

      public Book updateBook(Book newbook, List<StorehouseBook> storehouseBooks) {
            Book oldBook = this.findById(newbook.getId());

            // FIXME
            // verificar senão existe ma maneira de fazer essa verificação abaixo apenas com
            // um metodo do repository. Ex: Procurar um livro com o mesmo titulo que está
            // sendo submetido, porém com o Id diferente do Id do livro que está sendo
            // atualizado
            if (!Objects.equals(oldBook.getTitle(), newbook.getTitle())
                        && this.existsByTitle(newbook.getTitle())) {
                  throw new ConflictException("Book title is already in use.");

            }

            newbook = this.prepareBook(newbook, storehouseBooks);
            // FIXME
            // ao inves de deletar todas as storehousesBooks e criar novas de novo, procurar
            // se existe uma maneira de atualizar apenas as necessarias
            this.updateStorehouseBook(newbook, storehouseBooks);

            Book book = this.bookRepository.save(this.fillUpdate(oldBook, newbook));
            this.createStorehouseBook(book, storehouseBooks);

            return book;
      }

      public PublishingCompany findPublisgingCompany(Book book) {
            PublishingCompany publishingCompany = this.publishingCompanyService
                        .searchById(book.getPublishingCompany().getId());

            return publishingCompany;
      }

      public List<Author> findAuthorsList(Book book) {
            List<Author> authors = new ArrayList<>();

            for (Author author : book.getAuthorList()) {
                  Author authorExist = this.authorService.searchById(author.getId());
                  authors.add(authorExist);
            }

            return authors;
      }

      public List<Storehouse> findStorehousesList(List<StorehouseBook> storehouseBooks) {
            List<Storehouse> storehouses = new ArrayList<>();

            for (StorehouseBook storehouseBook : storehouseBooks) {
                  Storehouse storehouseExist = this.storehouseService
                              .searchById(storehouseBook.getStorehouse().getId());
                  storehouseBook.setStorehouse(storehouseExist);
                  storehouses.add(storehouseExist);

            }

            return storehouses;
      }

      public void createStorehouseBook(Book book, List<StorehouseBook> storehouseBooks) {
            for (StorehouseBook storehouseBook : storehouseBooks) {
                  storehouseBook.setBook(book);
                  storehouseBookRepository.save(storehouseBook);
            }

      }

      @Transactional
      public void updateStorehouseBook(Book book, List<StorehouseBook> storehouseBooks) {
            List<StorehouseBook> storehouseBookList = this.storehouseBookRepository.findAllByBookId(book.getId());

            this.storehouseBookRepository.deleteAll(storehouseBookList);

      }

      private Book fillUpdate(Book oldBook, Book newBook) {
            oldBook.setTitle(newBook.getTitle());
            oldBook.setYear(newBook.getYear());
            oldBook.setPrice(newBook.getPrice());
            oldBook.setCategories(newBook.getCategories());
            oldBook.setPublishingCompany(newBook.getPublishingCompany());
            oldBook.setAuthorList(newBook.getAuthorList());
            oldBook.setStorehouseList(newBook.getStorehouseList());

            return oldBook;
      }

      private Book prepareBook(Book book, List<StorehouseBook> storehouseBooks) {
            PublishingCompany publishingCompany = this.findPublisgingCompany(book);
            List<Author> authors = this.findAuthorsList(book);
            List<Storehouse> storehouses = this.findStorehousesList(storehouseBooks);

            book.setPublishingCompany(publishingCompany);
            book.setAuthorList(authors);
            book.setStorehouseList(storehouses);

            return book;
      }

}
