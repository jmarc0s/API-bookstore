package br.com.jmarcos.bookstore.groceryCart;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.model.intermediateClass.GroceryCartBook;
import br.com.jmarcos.bookstore.repository.GroceryCartRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.GroceryCartBookRepository;
import br.com.jmarcos.bookstore.service.BookService;
import br.com.jmarcos.bookstore.service.GroceryCartService;
import br.com.jmarcos.bookstore.service.PersonService;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GroceryCartServiceTest {

    @InjectMocks
    private GroceryCartService groceryCartService;

    @Mock
    private GroceryCartRepository groceryCartRepository;

    @Mock
    private BookService bookService;

    @Mock
    private PersonService personService;

    @Mock
    private GroceryCartBookRepository groceryCartBookRepository;


    @Test
    void searchByPersonId_returns_AllGroceryCartsByPersonId_WhenSuccessful() {
        GroceryCart groceryCart = createGroceryCart();
        List<GroceryCart> groceryCarts = List.of(groceryCart);
        when(groceryCartRepository.findAllByPersonId(anyLong())).thenReturn(groceryCarts);

        List<GroceryCart>  returnedGroceryCartList = this.groceryCartService.searchByPersonId(groceryCart.getPerson().getId());

        Assertions.assertNotNull(returnedGroceryCartList);
        Assertions.assertFalse(returnedGroceryCartList.isEmpty());
        Assertions.assertEquals(returnedGroceryCartList.get(0).getId(), groceryCarts.get(0).getId());
        Assertions.assertTrue(returnedGroceryCartList.get(0).getBooks().isEmpty());
        Assertions.assertEquals(returnedGroceryCartList.get(0).getPerson().getId(), groceryCarts.get(0).getPerson().getId());
        verify(groceryCartRepository).findAllByPersonId(groceryCart.getPerson().getId());

    }

    @Test
    void searchByIdAndPersonId_returns_AGroceryCartByIdAndPersonId_whenSuccessful(){
        GroceryCart groceryCart = createGroceryCart();
        when(groceryCartRepository.findByIdAndPersonId(anyLong(), anyLong())).thenReturn(Optional.of(groceryCart));

        GroceryCart returnedGroceryCart = this.groceryCartService.searchByIdAndPersonId(groceryCart.getId(), groceryCart.getPerson().getId());

        Assertions.assertNotNull(returnedGroceryCart);
        Assertions.assertEquals(returnedGroceryCart.getId(), groceryCart.getId());
        Assertions.assertTrue(returnedGroceryCart.getBooks().isEmpty());
        Assertions.assertEquals(returnedGroceryCart.getPerson().getId(), groceryCart.getPerson().getId());
        verify(groceryCartRepository).findByIdAndPersonId(groceryCart.getId(), groceryCart.getPerson().getId());
    }

    @Test
    void searchByIdAndPersonId_Throws_ResourceNotFoundException_WhenGroceryCartNotFound() {
        when(groceryCartRepository.findByIdAndPersonId(anyLong(), anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> groceryCartService.searchByIdAndPersonId(anyLong(), anyLong()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("GroceryCart not found with the given id"));
        
    }

    @Test
    void save_returns_ASavedGroceryCart_WhenSuccessful(){
        GroceryCart groceryCart = createGroceryCart();
        when(groceryCartRepository.save(any(GroceryCart.class))).thenReturn(groceryCart);
        when(personService.searchById(anyLong())).thenReturn(groceryCart.getPerson());

        GroceryCart savedGroceryCart = this.groceryCartService.save(groceryCart.getPerson().getId());


        Assertions.assertNotNull(savedGroceryCart);
        Assertions.assertEquals(savedGroceryCart.getId(), groceryCart.getId());
        Assertions.assertTrue(savedGroceryCart.getBooks().isEmpty());
        Assertions.assertEquals(savedGroceryCart.getPerson().getId(), groceryCart.getPerson().getId());
        verify(groceryCartRepository).save(any(GroceryCart.class));

    }

    @Test
    void save_withBooks_returns_ASavedGroceryCart_WhenSuccessful(){
        GroceryCart groceryCart = createGroceryCart();
        groceryCart.setBooks(creeateBookList());
        List<GroceryCartBook> groceryCartBooks = createGroceryCartBookList(groceryCart.getBooks().get(0).getId(), 20);
        when(groceryCartRepository.save(any(GroceryCart.class))).thenReturn(groceryCart);
        when(personService.searchById(anyLong())).thenReturn(groceryCart.getPerson());
        when(bookService.findById(anyLong())).thenReturn(groceryCart.getBooks().get(0));

        GroceryCart savedGroceryCart = this.groceryCartService.save(groceryCart, groceryCartBooks);


        Assertions.assertNotNull(savedGroceryCart);
        Assertions.assertEquals(savedGroceryCart.getId(), groceryCart.getId());
        Assertions.assertEquals(savedGroceryCart.getBooks(), groceryCart.getBooks());
        Assertions.assertEquals(savedGroceryCart.getPerson().getId(), groceryCart.getPerson().getId());
        verify(groceryCartRepository).save(groceryCart);
        verify(bookService).findById(anyLong());
        verify(groceryCartBookRepository).saveAll(anyList());

    }

    @Test
    void delete_deletesAGroceryCart_WhenSuccessful(){
        GroceryCart groceryCart = createGroceryCart();
        when(groceryCartRepository.findByIdAndPersonId(anyLong(), anyLong())).thenReturn(Optional.of(groceryCart));

        this.groceryCartService.deleteByIdAndPersonId(groceryCart.getId(), groceryCart.getPerson().getId());

        verify(groceryCartRepository).deleteById(groceryCart.getId());
        verify(groceryCartBookRepository).deleteAllByGroceryCartId(groceryCart.getId());

    }

    @Test
    void delete_Throws_ResourceNotFoundException_WhenGroceryCartNotFound(){
        when(groceryCartRepository.findByIdAndPersonId(anyLong(), anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> groceryCartService.deleteByIdAndPersonId(anyLong(), anyLong()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("GroceryCart not found with the given id"));
        
    }
    
    @Test
    void addBook_returns_AGroceryCartWithAnAddedBook_WhenSuccessful(){
        GroceryCart groceryCart = createGroceryCart();
        Book book = creeateBookList().get(0);
        when(groceryCartRepository.save(any(GroceryCart.class))).thenReturn(groceryCart);
        when(bookService.findById(book.getId())).thenReturn(book);

        GroceryCart returnedGroceryCart = this.groceryCartService.addBook(groceryCart, book.getId(), 10);

        Assertions.assertNotNull(returnedGroceryCart);
        Assertions.assertEquals(returnedGroceryCart.getId(), groceryCart.getId());
        Assertions.assertEquals(returnedGroceryCart.getBooks().get(0), book);
        Assertions.assertEquals(returnedGroceryCart.getPerson().getId(), groceryCart.getPerson().getId());
        verify(groceryCartRepository).save(groceryCart);
        verify(bookService).findById(anyLong());
        verify(groceryCartBookRepository).save(any(GroceryCartBook.class));

    }

    @Test
    void addBook_Throws_ConflictException_WhenBookIsAlreadyInGroceryCart(){
        GroceryCart groceryCart = createGroceryCart();
        Book book = creeateBookList().get(0);
        when(groceryCartBookRepository.findByGroceryCartIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(new GroceryCartBook()));
        when(bookService.findById(book.getId())).thenReturn(book);

           ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> groceryCartService.addBook(groceryCart, book.getId(), 10));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("Book is already in this grocery cart"));

    }

    @Test
    void deleteBook_returnsAGroceryCartWithADeletedBook_WhenSuccessful(){
        GroceryCart groceryCart = createGroceryCart();
        Book book = creeateBookList().get(0);
        groceryCart.getBooks().add(book);
        when(bookService.findById(book.getId())).thenReturn(book);
        when(groceryCartRepository.save(any(GroceryCart.class))).thenReturn(groceryCart);

        GroceryCart returnedGroceryCart = this.groceryCartService.deleteBook(groceryCart, book.getId());

        Assertions.assertNotNull(returnedGroceryCart);
        Assertions.assertEquals(returnedGroceryCart.getId(), groceryCart.getId());
        Assertions.assertTrue(returnedGroceryCart.getBooks().isEmpty());
        Assertions.assertEquals(returnedGroceryCart.getPerson().getId(), groceryCart.getPerson().getId());
        verify(groceryCartRepository).save(groceryCart);
        verify(bookService).findById(anyLong());
        verify(groceryCartBookRepository).deleteByGroceryCartIdAndBookId(groceryCart.getId(), book.getId());

    }

    @Test
    void updateBook_returnsAGroceryCartWithAnUpdatedBook_WhenSucceful(){
        GroceryCart groceryCart = createGroceryCart();
        Book book = creeateBookList().get(0);
        groceryCart.getBooks().add(book);
        GroceryCartBook groceryCartBook = createGroceryCartBook(groceryCart, book);
        when(bookService.findById(book.getId())).thenReturn(book);
        when(groceryCartBookRepository.findByGroceryCartIdAndBookId(groceryCart.getId(), book.getId())).thenReturn(Optional.of(groceryCartBook));

        GroceryCart updatedGroceryCart = this.groceryCartService.updateBook(groceryCart, book.getId(), 20);


        Assertions.assertNotNull(updatedGroceryCart);
        Assertions.assertEquals(updatedGroceryCart.getId(), groceryCart.getId());
        Assertions.assertEquals(updatedGroceryCart.getBooks(), groceryCart.getBooks());
        Assertions.assertEquals(updatedGroceryCart.getPerson().getId(), groceryCart.getPerson().getId());
        verify(groceryCartBookRepository).save(groceryCartBook);
        verify(bookService).findById(anyLong());
        verify(groceryCartBookRepository).findByGroceryCartIdAndBookId(groceryCart.getId(), book.getId());

    }

    @Test
    void updateBook_Throws_ResourceNotFoundException_WhenBookNotFoundInGroceryCart(){
        GroceryCart groceryCart = createGroceryCart();
        Book book = creeateBookList().get(0);
        when(bookService.findById(book.getId())).thenReturn(book);

           ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> groceryCartService.updateBook(groceryCart, book.getId(), 10));

        Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("book not found in this grocery cart"));

    }


    GroceryCart createGroceryCart(){
        GroceryCart groceryCart = new GroceryCart();

        groceryCart.setId(1L);
        groceryCart.setPerson(createPerson());

        return groceryCart;
    }

    List<Book> creeateBookList(){
        Book book = new Book();

        book.setId(1L);

        return List.of(book);
    }

    Person createPerson(){
        Person person = new Person();

        person.setId(1L);

        return person;
    }

    List<GroceryCartBook> createGroceryCartBookList(Long bookId, Integer quantity){
        GroceryCartBook groceryCartBook = new GroceryCartBook();
        Book book = new Book();
        book.setId(bookId);
        groceryCartBook.setBook(book);
        groceryCartBook.setQuantity(quantity);

        return List.of(groceryCartBook);
    }

    GroceryCartBook createGroceryCartBook(GroceryCart groceryCart, Book book) {
        GroceryCartBook groceryCartBook = new GroceryCartBook();
        
        groceryCart.setId(1L);
        groceryCartBook.setGroceryCart(groceryCart);
        groceryCartBook.setBook(book);
        groceryCartBook.setQuantity(10);

        return groceryCartBook;
    }

}
