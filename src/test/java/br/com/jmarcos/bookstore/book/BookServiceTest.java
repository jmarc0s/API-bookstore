package br.com.jmarcos.bookstore.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.jmarcos.bookstore.controller.dto.book.BookUpdateDTO;
import br.com.jmarcos.bookstore.controller.dto.storehouseBookDTO.StorehouseBookDTO;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.model.intermediateClass.StorehouseBook;
import br.com.jmarcos.bookstore.repository.BookRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.StorehouseBookRepository;
import br.com.jmarcos.bookstore.service.AuthorService;
import br.com.jmarcos.bookstore.service.BookService;
import br.com.jmarcos.bookstore.service.PublishingCompanyService;
import br.com.jmarcos.bookstore.service.StorehouseService;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PublishingCompanyService publishingCompanyService;

    @Mock
    private AuthorService authorService;

    @Mock
    private StorehouseService storehouseService;

    @Mock
    private StorehouseBookRepository storehouseBookRepository;

    // FIXME
    // org.mockito.exceptions.misusing.PotentialStubbingProblem:
    // Strict stubbing
    // argument mismatch.
    // Please check:-this invocation
    // of'findAll'method:bookRepository.findAll(org.springframework.data.jpa.domain.SpecificationComposition$$Lambda$458/0x0000000800e366a8
    // @71 a06021,Page request[number:0,size 5,sort:UNSORTED]

    @Test
    void search_returns_AllBooks_WhenSuccessful() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Book> bookList = List.of(createBook());
        PageImpl<Book> bookPage = new PageImpl<>(bookList);
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<Book> all = bookService.search(pageable, null, null, null);
        List<Book> bookSavedList = all.stream().toList();

        Assertions.assertFalse(bookSavedList.isEmpty());
        Assertions.assertEquals(bookList.get(0).getTitle(), bookSavedList.get(0).getTitle());
        Assertions.assertEquals(bookList.get(0).getYear(), bookSavedList.get(0).getYear());
        Assertions.assertEquals(bookList.get(0).getAuthorList(), bookSavedList.get(0).getAuthorList());
        Assertions.assertEquals(bookList.get(0).getGroceryCarts(), bookSavedList.get(0).getGroceryCarts());
        Assertions.assertEquals(bookList.get(0).getPrice(), bookSavedList.get(0).getPrice());
        Assertions.assertEquals(bookList.get(0).getPublishingCompany(), bookSavedList.get(0).getPublishingCompany());
        Assertions.assertEquals(bookList.get(0).getStorehouseList(), bookSavedList.get(0).getStorehouseList());

        verify(bookRepository).findAll(pageable);

    }

    @Test
    void findByTitle_returns_ABookByTheGivenTitle_WhenSuccessful() {
        Book book = createBook();
        when(bookRepository.findByTitle(anyString())).thenReturn(Optional.of(book));
        Book returnedBook = bookService.findByTitle(book.getTitle());

        Assertions.assertNotNull(returnedBook);
        Assertions.assertEquals(book.getTitle(), returnedBook.getTitle());
        Assertions.assertEquals(book.getYear(), returnedBook.getYear());
        Assertions.assertEquals(book.getAuthorList(), returnedBook.getAuthorList());
        Assertions.assertEquals(book.getGroceryCarts(), returnedBook.getGroceryCarts());
        Assertions.assertEquals(book.getPrice(), returnedBook.getPrice());
        Assertions.assertEquals(book.getPublishingCompany(), returnedBook.getPublishingCompany());
        Assertions.assertEquals(book.getStorehouseList(), returnedBook.getStorehouseList());

        verify(bookRepository).findByTitle(book.getTitle());

    }

    @Test
    void findByTitle_Throws_ResourceNotFoundException_WhenBookNotFound() {
        when(bookRepository.findByTitle(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> bookService.findByTitle(anyString()));

        Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Book not found in database with the specified title"));

    }

    @Test
    void findByAuthorName_returns_ABookListByTheGivenAuthorName_WhenSuccessful() {
        Book book = createBook();
        when(bookRepository.findAllByAuthorListName(anyString())).thenReturn(List.of(book));

        List<Book> returnedBookList = bookService.findByAuthorName(book.getAuthorList().get(0).getName());

        Assertions.assertFalse(returnedBookList.isEmpty());
        Assertions.assertEquals(book.getTitle(), returnedBookList.get(0).getTitle());
        Assertions.assertEquals(book.getYear(), returnedBookList.get(0).getYear());
        Assertions.assertEquals(book.getAuthorList(), returnedBookList.get(0).getAuthorList());
        Assertions.assertEquals(book.getGroceryCarts(), returnedBookList.get(0).getGroceryCarts());
        Assertions.assertEquals(book.getPrice(), returnedBookList.get(0).getPrice());
        Assertions.assertEquals(book.getPublishingCompany(), returnedBookList.get(0).getPublishingCompany());
        Assertions.assertEquals(book.getStorehouseList(), returnedBookList.get(0).getStorehouseList());

        verify(bookRepository).findAllByAuthorListName(book.getAuthorList().get(0).getName());

    }

    @Test
    void save_returnsASavedBook_WhenSuccessful() {
        Book book = createBook();
        when(publishingCompanyService.searchById(anyLong())).thenReturn(book.getPublishingCompany());
        when(authorService.searchById(anyLong())).thenReturn(book.getAuthorList().get(0));
        when(storehouseService.searchByID(anyLong())).thenReturn(book.getStorehouseList().get(0));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book savedBook = this.bookService.save(book, createStorehouseBookDTOs()
                .stream()
                .map(storehouseBookDTO -> storehouseBookDTO.toStorehouseBook())
                .collect(Collectors.toList()));

        Assertions.assertNotNull(savedBook);
        Assertions.assertEquals(book.getTitle(), savedBook.getTitle());
        Assertions.assertEquals(book.getYear(), savedBook.getYear());
        Assertions.assertEquals(book.getAuthorList(), savedBook.getAuthorList());
        Assertions.assertEquals(book.getGroceryCarts(), savedBook.getGroceryCarts());
        Assertions.assertEquals(book.getPrice(), savedBook.getPrice());
        Assertions.assertEquals(book.getPublishingCompany(), savedBook.getPublishingCompany());
        Assertions.assertEquals(book.getStorehouseList(), savedBook.getStorehouseList());
        verify(bookRepository).save(book);
        verify(publishingCompanyService).searchById(book.getPublishingCompany().getId());
        verify(authorService).searchById(book.getAuthorList().get(0).getId());
        verify(storehouseService).searchByID(book.getStorehouseList().get(0).getId());
        verify(storehouseBookRepository).save(any(StorehouseBook.class));

    }

    @Test
    void save_Throws_ConflictException_WhenBooktitleIsAlreadyInUse() {
        Book book = createBook();
        Book newbook = createBook();
        when(bookRepository.findByTitle(anyString())).thenReturn(Optional.of(book));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> bookService.save(newbook, createStorehouseBookDTOs()
                                .stream()
                                .map(storehouseBookDTO -> storehouseBookDTO.toStorehouseBook())
                                .collect(Collectors.toList())));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("Book title is already in use"));

    }

    @Test
    void findById_returns_ABookByTheGivenId_WhenSuccessful() {
        Book book = createBook();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        Book returnedBook = bookService.findById(book.getId());

        Assertions.assertNotNull(returnedBook);
        Assertions.assertEquals(book.getTitle(), returnedBook.getTitle());
        Assertions.assertEquals(book.getYear(), returnedBook.getYear());
        Assertions.assertEquals(book.getAuthorList(), returnedBook.getAuthorList());
        Assertions.assertEquals(book.getGroceryCarts(), returnedBook.getGroceryCarts());
        Assertions.assertEquals(book.getPrice(), returnedBook.getPrice());
        Assertions.assertEquals(book.getPublishingCompany(), returnedBook.getPublishingCompany());
        Assertions.assertEquals(book.getStorehouseList(), returnedBook.getStorehouseList());

        verify(bookRepository).findById(book.getId());

    }

    @Test
    void findById_Throws_ResourceNotFoundException_WhenBookNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> bookService.findById(anyLong()));

        Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Book not found in database with the specified id"));

    }

    @Test
    void deleteById_deletesABookByTheGivenId_WhenSuccessful() {
        Book book = createBook();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        this.bookService.deleteById(book.getId());

        verify(bookRepository).deleteById(book.getId());
        verify(storehouseBookRepository).deleteAllByBookId(anyLong());
        verify(bookRepository).findById(book.getId());
    }

    @Test
    void deleteById_Throws_ResourceNotFoundException_WhenBookNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> bookService.deleteById(anyLong()));

        Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Book not found in database with the specified id"));

    }

    @Test
    void update_returnsAnUpdatedBook_WhenSuccessful() {
        Book book = createBook();
        BookUpdateDTO bookUpdateDTO = createBookUpdateDTO();
        Book updateBook = bookUpdateDTO.toBook(1L);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(publishingCompanyService.searchById(anyLong())).thenReturn(updateBook.getPublishingCompany());
        when(authorService.searchById(anyLong())).thenReturn(updateBook.getAuthorList().get(0));
        when(storehouseService.searchByID(anyLong())).thenReturn(updateBook.getStorehouseList().get(0));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book updatedBook = this.bookService.updateBook(updateBook, createStorehouseBookDTOs()
                .stream()
                .map(storehouseBookDTO -> storehouseBookDTO.toStorehouseBook())
                .collect(Collectors.toList()));

        Assertions.assertNotNull(updatedBook);
        Assertions.assertEquals(updateBook.getTitle(), updatedBook.getTitle());
        Assertions.assertEquals(updateBook.getYear(), updatedBook.getYear());
        Assertions.assertEquals(updateBook.getAuthorList(), updatedBook.getAuthorList());
        Assertions.assertEquals(updateBook.getGroceryCarts(), updatedBook.getGroceryCarts());
        Assertions.assertEquals(updateBook.getPrice(), updatedBook.getPrice());
        Assertions.assertEquals(updateBook.getPublishingCompany(), updatedBook.getPublishingCompany());
        Assertions.assertEquals(updateBook.getStorehouseList(), updatedBook.getStorehouseList());

        verify(storehouseBookRepository).deleteAll(anyList());
        verify(bookRepository).save(book);
        verify(publishingCompanyService).searchById(updateBook.getPublishingCompany().getId());
        verify(authorService).searchById(updateBook.getAuthorList().get(0).getId());
        verify(storehouseService).searchByID(updateBook.getStorehouseList().get(0).getId());
        verify(storehouseBookRepository).save(any(StorehouseBook.class));

    }

    @Test
    void update_Throws_ConflictException_WhenBookTitleIsAlreadyInUse() {
        Book book = createBook();
        BookUpdateDTO bookUpdateDTO = createBookUpdateDTO();
        Book updateBook = bookUpdateDTO.toBook(1L);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRepository.findByTitle(anyString())).thenReturn(Optional.of(book));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> bookService.updateBook(updateBook, createStorehouseBookDTOs()
                                .stream()
                                .map(storehouseBookDTO -> storehouseBookDTO.toStorehouseBook())
                                .collect(Collectors.toList())));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("Book title is already in use."));

    }

    Book createBook() {
        Book book = new Book();

        book.setId(1L);
        book.setPrice(new BigDecimal(50.00));
        book.setTitle("Livro legal");
        book.setYear(2004);
        book.setStorehouseList(createStorehouseList());
        book.setPublishingCompany(createPublishingCompany());
        book.setAuthorList(createAuthorList());

        return book;

    }

    BookUpdateDTO createBookUpdateDTO() {
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        Set<Long> authorIdList = new HashSet<>();
        authorIdList.add(2L);

        bookUpdateDTO.setPrice(new BigDecimal(60.00));
        bookUpdateDTO.setTitle("Livro nem tão legal");
        bookUpdateDTO.setYear(2005);
        bookUpdateDTO.setPublishingCompanyId(2L);
        bookUpdateDTO.setAuthorIdList(authorIdList);
        bookUpdateDTO.setStorehouseBookDTOs(createStorehouseBookDTOs());

        return bookUpdateDTO;

    }

    List<Storehouse> createStorehouseList() {
        Storehouse storehouse = new Storehouse();
        storehouse.setId(1L);

        return List.of(storehouse);
    }

    Set<StorehouseBookDTO> createStorehouseBookDTOs() {
        StorehouseBookDTO storehouseBook = new StorehouseBookDTO();
        storehouseBook.setStorehouseId(1L);
        storehouseBook.setQuantity(10);

        return Set.of(storehouseBook);
    }

    List<Author> createAuthorList() {
        Author author = new Author();
        author.setId(1L);
        author.setName("J.K");

        return List.of(author);

    }

    PublishingCompany createPublishingCompany() {
        PublishingCompany publishingCompany = new PublishingCompany();
        publishingCompany.setId(1L);

        return publishingCompany;
    }

}
