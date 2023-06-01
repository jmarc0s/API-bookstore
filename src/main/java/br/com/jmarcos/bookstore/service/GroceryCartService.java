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
import br.com.jmarcos.bookstore.repository.BookRepository;
import br.com.jmarcos.bookstore.repository.GroceryCartRepository;
import br.com.jmarcos.bookstore.repository.PersonRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.GroceryCartBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class GroceryCartService {
    private final GroceryCartRepository groceryCartRepository;
    private final BookRepository bookRepository;
    private final PersonRepository personRepository;
    private final GroceryCartBookRepository groceryCartBookRepository;

    @Autowired
    public GroceryCartService(GroceryCartRepository groceryCartRepository, BookRepository bookRepository,
            GroceryCartBookRepository groceryCartBookRepository,
            PersonRepository personRepository) {
        this.groceryCartRepository = groceryCartRepository;
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
        this.groceryCartBookRepository = groceryCartBookRepository;
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
            .orElseThrow(() -> new ResourceNotFoundException("GroceryCart not found with the iven id"));
    }

    public Optional<GroceryCart> save(GroceryCart groceryCart, List<Integer> quantities) {
        List<Book> books = this.searchBooks(groceryCart);
        Optional<Person> person = this.personRepository.findById(groceryCart.getPerson().getId());
        if (books != null) {
            groceryCart.setPerson(person.get());
            groceryCart.setBooks(books);
            System.out.println(quantities);
            GroceryCart groceryCartOptional = this.groceryCartRepository.save(groceryCart);
            this.createGroceryCartBook(groceryCart, quantities);
            return Optional.of(groceryCartOptional);
        }
        return Optional.empty();

    }

    public GroceryCart save(Long personId) {
        GroceryCart groceryCart = new GroceryCart();
        Optional<Person> person = this.personRepository.findById(personId);

        groceryCart.setPerson(person.get());

        this.groceryCartRepository.save(groceryCart);
        return groceryCart;
    }

    private List<Book> searchBooks(GroceryCart groceryCart) {
        List<Book> books = new ArrayList<>();

        for (Book book : groceryCart.getBooks()) {
            Optional<Book> bookExist = this.bookRepository.findById(book.getId());
            if (bookExist.isPresent()) {
                books.add(bookExist.get());
            } else {
                books = null;
                break;
            }
        }

        return books;
    }

    public Optional<GroceryCart> addBook(GroceryCart groceryCart, Long bookId, int quantity) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        Optional<GroceryCartBook> groceryCartBook = this.groceryCartBookRepository
                .findByGroceryCartIdAndBookId(groceryCart.getId(), bookId);
        if (book.isPresent() && groceryCartBook.isEmpty()) {
            groceryCart.getBooks().add(book.get());
            newGroceryCartbook(groceryCart, book.get(), quantity);
            return Optional.of(this.groceryCartRepository.save(groceryCart));
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<GroceryCart> deleteBook(GroceryCart groceryCart, Long bookId) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isPresent()) {
            this.deleteGroceryCartBook(groceryCart, book.get());
            groceryCart.getBooks().remove(book.get());
            return Optional.of(this.groceryCartRepository.save(groceryCart));
        }
        return Optional.empty();
    }

    public Optional<GroceryCart> updateBook(GroceryCart groceryCart, Long bookId, int quantity) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isPresent()) {
            if (this.updateGroceryCartBook(groceryCart, book.get(), quantity)) {
                return Optional.of(groceryCart);
            }
            return Optional.empty();
        }
        return Optional.empty();
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
        int index = 0;

        System.out.println(quantities);
        for (Book book : groceryCart.getBooks()) {
            GroceryCartBook groceryCartBook = new GroceryCartBook();
            groceryCartBook.setGroceryCart(groceryCart);
            groceryCartBook.setBook(book);
            groceryCartBook.setQuantity(quantities.get(index));
            index++;
            this.groceryCartBookRepository.save(groceryCartBook);
        }
        index = 0;
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
