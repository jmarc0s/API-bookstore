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
import br.com.jmarcos.bookstore.repository.intermediateClass.GroceryCartBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class GroceryCartService {
    private final GroceryCartRepository groceryCartRepository;
    private final BookService bookService;
    private final PersonService personService;
    private final GroceryCartBookRepository groceryCartBookRepository;

    @Autowired
    public GroceryCartService(GroceryCartRepository groceryCartRepository,
            GroceryCartBookRepository groceryCartBookRepository,
            PersonService personService, BookService bookService) {
        this.groceryCartRepository = groceryCartRepository;
        this.personService = personService;
        this.groceryCartBookRepository = groceryCartBookRepository;
        this.bookService = bookService;
    }

    public List<GroceryCart> searchByPersonId(Long personId) {
        return this.groceryCartRepository.findAllByPersonId(personId);
    }

    @Transactional
    public void deleteByIdAndPersonId(Long id, Long personId) {
        GroceryCart groceryCart = this.searchByIdAndPersonId(id, personId);

        this.groceryCartBookRepository.deleteAllByGroceryCartId(groceryCart.getId());
        this.groceryCartRepository.deleteById(groceryCart.getId());

    }

    public GroceryCart searchByIdAndPersonId(Long id, Long personId) {
        return this.groceryCartRepository.findByIdAndPersonId(id, personId)
                .orElseThrow(() -> new ResourceNotFoundException("GroceryCart not found with the given id"));
    }

    public GroceryCart save(GroceryCart groceryCart, List<GroceryCartBook> groceryCartBooks) {
        List<Book> books = this.searchBooks(groceryCartBooks);
        Person person = this.personService.searchById(groceryCart.getPerson().getId());

        groceryCart.setPerson(person);
        groceryCart.setBooks(books);

        GroceryCart savedGroceryCart = this.groceryCartRepository.save(groceryCart);
        this.createGroceryCartBook(savedGroceryCart, groceryCartBooks);

        return savedGroceryCart;

    }

    public GroceryCart save(Long personId) {
        GroceryCart groceryCart = new GroceryCart();
        Person person = this.personService.searchById(personId);

        groceryCart.setPerson(person);

        return this.groceryCartRepository.save(groceryCart);
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

    private void createGroceryCartBook(GroceryCart groceryCart, List<GroceryCartBook> groceryCartBooks) {
        groceryCartBooks.forEach(groceryCartBook -> groceryCartBook.setGroceryCart(groceryCart));

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


    private List<Book> searchBooks(List<GroceryCartBook> groceryCartBooks) {
        List<Book> books = new ArrayList<>();

        for (GroceryCartBook groceryCartBook : groceryCartBooks) {

            Book bookExists = this.bookService.findById(groceryCartBook.getBook().getId());
            groceryCartBook.setBook(bookExists);
            books.add(bookExists);

        }

        return books;
    }
    
}
