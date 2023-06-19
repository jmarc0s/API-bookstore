package br.com.jmarcos.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.model.intermediateClass.StorehouseBook;
import br.com.jmarcos.bookstore.repository.BookRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.StorehouseBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final PublishingCompanyService publishingCompanyService;
    private final AuthorService authorService;
    private final StorehouseService storehouseService;
    private final StorehouseBookRepository storehouseBookRepository;

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

    public boolean existsByTitle(String title) {
        Optional<Book> exists = this.bookRepository.findByTitle(title);

        return exists.isPresent();
    }

    public Page<Book> search(Pageable pageable) {
        return this.bookRepository.findAll(pageable);
    }

    public Book findByTitle(String title) {
        return this.bookRepository.findByTitle(title)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Book not found in database with the specified title"));
    }

    public List<Book> findByAuthorName(String authorName) {
        return this.bookRepository.findAllByAuthorListName(authorName);
    }

    public Book save(Book book, List<Integer> quantityList) {
        if (this.existsByTitle(book.getTitle())) {
            throw new ConflictException("Book title is already in use");
        }

        PublishingCompany publishingCompany = this.findPublisgingCompany(book);
        List<Author> authors = this.findAuthorsList(book);
        List<Storehouse> storehouses = this.findStorehousesList(book);

        book.setPublishingCompany(publishingCompany);
        book.setAuthorList(authors);
        book.setStorehouseList(storehouses);

        book = this.bookRepository.save(book);
        this.createStorehouseBook(book, quantityList);

        return book;
    }

    public Book findById(Long id) {
        return this.bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found in database with the specified id"));
    }

    @Transactional
    public void deleteById(Long id) {
        Book book = this.findById(id);

        this.deleteStorehouseBook(book.getId());
        this.bookRepository.deleteById(book.getId());
    }

    public Book updateBook(Book newbook, List<Integer> quantityList) {
        Book oldBook = this.findById(newbook.getId());

        if (!Objects.equals(oldBook.getTitle(), newbook.getTitle())
                && this.existsByTitle(newbook.getTitle())) {
            throw new ConflictException("Book title is already in use.");

        }

        this.updateStorehouseBook(newbook, quantityList);

        Book book = this.save(this.fillUpdate(oldBook, newbook), quantityList);

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

    public List<Storehouse> findStorehousesList(Book book) {
        List<Storehouse> storehouses = new ArrayList<>();

        for (Storehouse storehouse : book.getStorehouseList()) {
            Storehouse storehouseExist = this.storehouseService.searchByID(storehouse.getId());
            storehouses.add(storehouseExist);

        }

        return storehouses;
    }

    public void createStorehouseBook(Book book, List<Integer> quantityList) {
        int index = 0;

        for (Storehouse storehouse : book.getStorehouseList()) {
            StorehouseBook storehouseBook = new StorehouseBook();
            storehouseBook.setStorehouse(storehouse);
            storehouseBook.setBook(book);
            storehouseBook.setQuantity(quantityList.get(index));
            index++;
            storehouseBookRepository.save(storehouseBook);
        }

    }

    public void deleteStorehouseBook(Long id) {
        this.storehouseBookRepository.deleteAllByBookId(id);
    }

    @Transactional
    public void updateStorehouseBook(Book book, List<Integer> quantityList) {
        List<StorehouseBook> storehouseBookList = this.storehouseBookRepository.findAllByBookId(book.getId());

        PublishingCompany publishingCompany = this.findPublisgingCompany(book);
        List<Author> authors = this.findAuthorsList(book);
        List<Storehouse> storehouses = this.findStorehousesList(book);

        this.storehouseBookRepository.deleteAll(storehouseBookList);

    }

    private Book fillUpdate(Book oldBook, Book newBook) {
        oldBook.setTitle(newBook.getTitle());
        oldBook.setYear(newBook.getYear());
        oldBook.setPrice(newBook.getPrice());
        oldBook.setPublishingCompany(newBook.getPublishingCompany());
        oldBook.setAuthorList(newBook.getAuthorList());
        oldBook.setStorehouseList(newBook.getStorehouseList());

        return oldBook;
    }

    public List<Book> searchByPublishingCompany(Long id) {
        return this.bookRepository.findAllByPublishingCompanyId(id);
    }

}
