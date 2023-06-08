package br.com.jmarcos.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import br.com.jmarcos.bookstore.repository.GroceryCartRepository;
import br.com.jmarcos.bookstore.repository.PersonRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.GroceryCartBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class GroceryCartService {
    private final GroceryCartRepository groceryCartRepository;
    private final BookService bookService;
    private final PersonRepository personRepository;
    private final GroceryCartBookRepository groceryCartBookRepository;

    @Autowired
    public GroceryCartService(GroceryCartRepository groceryCartRepository,
            GroceryCartBookRepository groceryCartBookRepository,
            PersonRepository personRepository, BookService bookService) {
        this.groceryCartRepository = groceryCartRepository;
        this.personRepository = personRepository;
        this.groceryCartBookRepository = groceryCartBookRepository;
        this.bookService = bookService;
    }

    public List<GroceryCart> searchPersonId(Long personId) {
        return this.groceryCartRepository.findAllByPersonId(personId);
    }

    @Transactional
    public void deleteByIdAndPersonId(Long id, Long personId) {
        GroceryCart exists = this.searchByIdAndPersonId(id, personId);

        this.groceryCartBookRepository.deleteAllByGroceryCartId(exists.getId());
        this.groceryCartRepository.deleteById(exists.getId());

    }

    public GroceryCart searchByIdAndPersonId(Long id, Long personId) {
        return this.groceryCartRepository.findByIdAndPersonId(id, personId)
                .orElseThrow(() -> new ResourceNotFoundException("GroceryCart not found with the given id"));
    }

    public GroceryCart save(GroceryCart groceryCart, List<Integer> quantities) {
        List<Book> books = this.searchBooks(groceryCart);
        Optional<Person> person = this.personRepository.findById(groceryCart.getPerson().getId());

        groceryCart.setPerson(person.get());
        groceryCart.setBooks(books);

        GroceryCart savedGroceryCart = this.groceryCartRepository.save(groceryCart);
        this.createGroceryCartBook(savedGroceryCart, quantities);

        return savedGroceryCart;

    }

    public GroceryCart save(Long personId) {
        GroceryCart groceryCart = new GroceryCart();
        Optional<Person> person = this.personRepository.findById(personId);

        groceryCart.setPerson(person.get());

        return this.groceryCartRepository.save(groceryCart);
    }

    private List<Book> searchBooks(GroceryCart groceryCart) {
        List<Book> books = new ArrayList<>();

        for (Book book : groceryCart.getBooks()) {

            Book bookExists = this.bookService.findById(book.getId());
            books.add(bookExists);

        }

        return books;
    }

    public GroceryCart addBook(GroceryCart groceryCart, Long bookId, int quantity) {
        Optional<GroceryCartBook> groceryCartBook = this.groceryCartBookRepository
                .findByGroceryCartIdAndBookId(groceryCart.getId(), bookId);
        Book book = this.bookService.findById(bookId);

        if (groceryCartBook.isPresent()) {
            throw new ConflictException("Book is already in this grocery cart");
        }

        groceryCart.getBooks().add(book);
        newGroceryCartbook(groceryCart, book, quantity);
        return this.groceryCartRepository.save(groceryCart);

    }

    @Transactional
    public GroceryCart deleteBook(GroceryCart groceryCart, Long bookId) {
        Book book = this.bookService.findById(bookId);

        this.deleteGroceryCartBook(groceryCart, book);
        groceryCart.getBooks().remove(book);

        return this.groceryCartRepository.save(groceryCart);
    }

    public GroceryCart updateBook(GroceryCart groceryCart, Long bookId, int quantity) {
        Book book = this.bookService.findById(bookId);

        if (this.updateGroceryCartBook(groceryCart, book, quantity)) {
            return groceryCart;
        }
        throw new ResourceNotFoundException("book not found in this grocery cart");

    }

    private Boolean updateGroceryCartBook(GroceryCart groceryCart, Book book, int quantity) {

        Optional<GroceryCartBook> groceryCartBookOp = this.groceryCartBookRepository
                .findByGroceryCartIdAndBookId(groceryCart.getId(), book.getId());

        if (groceryCartBookOp.isPresent()) {

            GroceryCartBook groceryCartBook = groceryCartBookOp.get();
            groceryCartBook.setQuantity(quantity);
            this.groceryCartBookRepository.save(groceryCartBook);

            return true;
        }

        return false;
    }

    private void deleteGroceryCartBook(GroceryCart groceryCart, Book book) {
        this.groceryCartBookRepository.deleteByGroceryCartIdAndBookId(groceryCart.getId(), book.getId());
    }

    private void createGroceryCartBook(GroceryCart groceryCart, List<Integer> quantities) {
        List<GroceryCartBook> groceryCartBooks = new ArrayList<>();
        int index = 0;
        for (Book book : groceryCart.getBooks()) {

            GroceryCartBook groceryCartBook = new GroceryCartBook();
            groceryCartBook.setGroceryCart(groceryCart);
            groceryCartBook.setBook(book);
            groceryCartBook.setQuantity(quantities.get(index));

            groceryCartBooks.add(groceryCartBook);

            index++;
        }

        this.groceryCartBookRepository.saveAll(groceryCartBooks);
    }

    private void newGroceryCartbook(GroceryCart groceryCart, Book book, int quantity) {
        GroceryCartBook groceryCartBook = new GroceryCartBook();
        groceryCartBook.setGroceryCart(groceryCart);
        groceryCartBook.setBook(book);
        groceryCartBook.setQuantity(quantity);
        this.groceryCartBookRepository.save(groceryCartBook);
    }

    public List<GroceryCartBook> listGroceryCartBook(GroceryCart groceryCart) {
        return this.groceryCartBookRepository.findAllByGroceryCartId(groceryCart.getId());
    }

}
