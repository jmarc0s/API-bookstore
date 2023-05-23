package br.com.jmarcos.bookstore.service;

import java.util.ArrayList;
import java.util.List;
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
import br.com.jmarcos.bookstore.repository.AuthorRepository;
import br.com.jmarcos.bookstore.repository.BookRepository;
import br.com.jmarcos.bookstore.repository.PublishingCompanyRepository;
import br.com.jmarcos.bookstore.repository.StorehouseRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.StorehouseBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final PublishingCompanyRepository publishingCompanyRepository;
    private final AuthorRepository authorRepository;
    private final StorehouseRepository storehouseRepository;
    private final StorehouseBookRepository storehouseBookRepository;

    @Autowired
    public BookService(BookRepository bookRepository,
            PublishingCompanyRepository publishingCompanyRepository,
            AuthorRepository authorRepository,
            StorehouseRepository storehouseRepository,
            StorehouseBookRepository storehouseBookRepository) {

        this.bookRepository = bookRepository;
        this.publishingCompanyRepository = publishingCompanyRepository;
        this.authorRepository = authorRepository;
        this.storehouseRepository = storehouseRepository;
        this.storehouseBookRepository = storehouseBookRepository;
    }

    public boolean existsByTitle(String title) {

        Optional<Book> exists = this.bookRepository.findByTitle(title);
        if (exists.isPresent()) {
            return true;
        }

        return false;
    }

    public Page<Book> search(Pageable pageable) {
        return this.bookRepository.findAll(pageable);
    }

    public Optional<Book> findByTitle(String title) {
        return this.bookRepository.findByTitle(title);
    }

    public List<Book> findByAuthorName(String authorName) {
        return this.bookRepository.findAllByAuthorListName(authorName);
    }

    public Optional<Book> save(Book book, List<Integer> quantityList) {
        System.out.println(book.getPublishingCompany());
        PublishingCompany publishingCompany = this.findPublisgingCompany(book);
        List<Author> authors = this.findAuthorsList(book);
        List<Storehouse> storehouses = this.findStorehousesList(book);

        if (publishingCompany != null && authors != null && storehouses != null) {
            book.setPublishingCompany(publishingCompany);
            book.setAuthorList(authors);
            book.setStorehouseList(storehouses);

            this.bookRepository.save(book);
            Boolean created = this.createStorehouseBook(book, quantityList);
            if (created) {
                return Optional.of(book);
            } else {
                return Optional.empty();
            }

        }
        return Optional.empty();
    }

    public Book findByid(Long id) {
        return this.bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found in database with the specified id"));
    }

    @Transactional
    public boolean deleteByid(Long id) {

        Optional<Book> exists = this.bookRepository.findById(id);
        if (exists.isPresent()) {
            this.deleteStorehouseBook(id);
            this.bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Book> updateBook(Book newbook, List<Integer> quantityList) {
        Optional<Book> oldBook = this.bookRepository.findById(newbook.getId());

        if (oldBook.isPresent()) {
            this.updateStorehouseBook(newbook, quantityList);
            oldBook = this.save(this.fillUpdate(oldBook.get(), newbook), quantityList);
        }

        return oldBook.isPresent()
                ? oldBook
                : Optional.empty();
    }

    public PublishingCompany findPublisgingCompany(Book book) {

        Optional<PublishingCompany> publishingCompany = this.publishingCompanyRepository
                .findById(book.getPublishingCompany().getId());

        return publishingCompany.isPresent()
                ? publishingCompany.get()
                : null;
    }

    public List<Author> findAuthorsList(Book book) {

        List<Author> authors = new ArrayList<>();

        for (Author author : book.getAuthorList()) {
            Optional<Author> authorExist = this.authorRepository.findById(author.getId());
            if (authorExist.isPresent()) {
                authors.add(authorExist.get());
            } else {
                authors = null;
                break;
            }
        }

        return authors;
    }

    public List<Storehouse> findStorehousesList(Book book) {
        List<Storehouse> storehouses = new ArrayList<>();
        for (Storehouse storehouse : book.getStorehouseList()) {
            Optional<Storehouse> storehouseExist = this.storehouseRepository.findById(storehouse.getId());

            if (storehouseExist.isPresent()) {
                storehouses.add(storehouseExist.get());
            } else {
                storehouses = null;
                break;
            }
        }

        return storehouses;
    }

    public Boolean createStorehouseBook(Book book, List<Integer> quantityList) {

        int index = 0;

        for (Storehouse storehouse : book.getStorehouseList()) {
            StorehouseBook storehouseBook = new StorehouseBook();
            storehouseBook.setStorehouse(storehouse);
            storehouseBook.setBook(book);
            storehouseBook.setQuantity(quantityList.get(index));
            index++;
            storehouseBookRepository.save(storehouseBook);
        }
        index = 0;
        return true;
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

        if (publishingCompany != null && authors != null && storehouses != null) {
            this.storehouseBookRepository.deleteAll(storehouseBookList);
        }
        // this.deleteStorehouseBook(book.getId());
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

    // public Optional<Book> testeSql(int pc) {
    // return this.bookRepository.testeSql(pc);
    // }

}
