package br.com.jmarcos.bookstore.author;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.jmarcos.bookstore.controller.dto.address.AddressUpdateDTO;
import br.com.jmarcos.bookstore.controller.dto.author.AuthorUpdateDTO;
import br.com.jmarcos.bookstore.model.Address;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.repository.AuthorRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.StorehouseBookRepository;
import br.com.jmarcos.bookstore.service.AuthorService;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {
     
    @InjectMocks
    private AuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private StorehouseBookRepository storehouseBookRepository;


    @Test
    void search_returns_AllAuthores_WhenSuccessful() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Author> authorList = List.of(createAuthor());
        PageImpl<Author> authorPage = new PageImpl<>(authorList);

        when(authorRepository.findAll(pageable)).thenReturn(authorPage);

        Page<Author> all = authorService.search(pageable);
        List<Author> auhorSavedList = all.stream().toList();

        Assertions.assertFalse(auhorSavedList.isEmpty());
        Assertions.assertEquals(authorList.get(0).getName(), auhorSavedList.get(0).getName());
        Assertions.assertEquals(authorList.get(0).getUrl(), auhorSavedList.get(0).getUrl());
        Assertions.assertEquals(authorList.get(0).getAddress(),
                auhorSavedList.get(0).getAddress());
        Assertions.assertEquals(authorList.get(0).getBookList(),
                auhorSavedList.get(0).getBookList());
        Assertions.assertNotNull(auhorSavedList.get(0).getId());

        verify(authorRepository).findAll(pageable);

    }

    @Test
    void save_returns_ASavedAuthor_WhenSuccessful() {
        Author author = createAuthor();
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author savedAuthor= authorService.save(author);

        Assertions.assertNotNull(savedAuthor);
        Assertions.assertNotNull(savedAuthor.getId());
        Assertions.assertEquals(author.getName(), savedAuthor.getName());
        Assertions.assertEquals(author.getAddress(), savedAuthor.getAddress());
        Assertions.assertEquals(author.getUrl(), savedAuthor.getUrl());
        Assertions.assertEquals(author.getBookList(), savedAuthor.getBookList());
        verify(authorRepository).save(author);

    }

    @Test
    void save_Throws_ConflictException_WhenAuthorNameIsAlreadyInUse() {
        Author author = createAuthor();
        Author newAuthor = createAuthor();
        when(authorRepository.findByName(anyString())).thenReturn(Optional.of(author));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> authorService.save(newAuthor));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("Author name is already in use"));

    }

    @Test
    void searchById_returns_AnAuthorTheGivenId_WhenSuccessful() {
        Author author = createAuthor();
        when(authorRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));

        Author returnedAuthor = this.authorService
                .searchById(author.getId());

        Assertions.assertNotNull(returnedAuthor);
        Assertions.assertNotNull(returnedAuthor.getId());
        Assertions.assertEquals(author.getName(), returnedAuthor.getName());
        Assertions.assertEquals(author.getAddress(), returnedAuthor.getAddress());
        Assertions.assertEquals(author.getUrl(), returnedAuthor.getUrl());
        Assertions.assertEquals(author.getBookList(), returnedAuthor.getBookList());
        verify(authorRepository).findById(author.getId());
    }

    @Test
    void searchById_Throws_ResourceNotFoundException_WhenAuthorNotFound() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> authorService.searchById(1L));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Author not found with the given id"));
        
    }

    @Test
    void searchByName_returns_AnAuthorTheGivenName_WhenSuccessful() {
        Author author = createAuthor();
        when(authorRepository.findByName(author.getName()))
                .thenReturn(Optional.of(author));

        Author returnedAuthor = this.authorService
                .searchByName(author.getName());

        Assertions.assertNotNull(returnedAuthor);
        Assertions.assertNotNull(returnedAuthor.getId());
        Assertions.assertEquals(author.getName(), returnedAuthor.getName());
        Assertions.assertEquals(author.getAddress(), returnedAuthor.getAddress());
        Assertions.assertEquals(author.getUrl(), returnedAuthor.getUrl());
        Assertions.assertEquals(author.getBookList(), returnedAuthor.getBookList());
        verify(authorRepository).findByName(author.getName());
    }

    @Test
    void searchByName_Throws_ResourceNotFoundException_WhenAuthorNotFound() {
        when(authorRepository.findByName(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> authorService.searchByName(anyString()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Author not found with the given name"));
        
    }

    @Test
    void deleteById_deletesAnAuthor_WhenSuccessful() {
        Author author = createAuthor();
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));

        this.authorService.deleteById(author.getId());

        verify(authorRepository).deleteById(author.getId());
        verify(storehouseBookRepository).deleteAllByBookId(anyLong());
        verify(authorRepository).findById(author.getId());
    }

    @Test
    void deleteById_Throws_ResourceNotFoundException_WhenAuthorNotFound() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> authorService.deleteById(anyLong()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Author not found with the given id"));
        
    }

    @Test
    void update_returns_AUpdatedAuthor_WhenSuccessful() {
        Author author = createAuthor();
        AuthorUpdateDTO authorUpdateDTO = createAuthorUpdateDTO();
        when(authorRepository.save(author)).thenReturn(author);
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));

        Author updatedAuthor = authorService.update(authorUpdateDTO.toAuthor(1L));

        Assertions.assertNotNull(updatedAuthor);
        Assertions.assertEquals(author.getId(), updatedAuthor.getId());
        Assertions.assertEquals(author.getName(), updatedAuthor.getName());
        Assertions.assertEquals(author.getUrl(), updatedAuthor.getUrl());
        Assertions.assertEquals(author.getAddress(), updatedAuthor.getAddress());
        verify(authorRepository).save(author);

    }

    @Test
    void update_Throws_ConflictException_WhenAuthorNameIsAlreadyInUse() {
        Author author = createAuthor();
        AuthorUpdateDTO authorUpdateDTO = createAuthorUpdateDTO();
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(authorRepository.findByName(anyString())).thenReturn(Optional.of(author));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> authorService.update(authorUpdateDTO.toAuthor(1L)));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("Author name is already in use"));

    }




    Author createAuthor() {
        Author author = new Author();

        author.setId(1L);
        author.setName("J.K");
        author.setUrl("JK.com");
        author.setAddress(new Address("rua b", 222, "fortaleza", "CE", "627000002"));
        author.getAddress().setId(1L);
        author.setBookList(createBookList());

        return author;
    }



    public List<Book> createBookList() {
        List<Book> bookList = new ArrayList<>();
        Book book = new Book();

        book.setId(1L);
        bookList.add(book);

        return bookList;

    }

    AuthorUpdateDTO createAuthorUpdateDTO() {
        AuthorUpdateDTO authorUpdateDTO = new AuthorUpdateDTO();

        authorUpdateDTO.setName("rock");
        authorUpdateDTO.setUrl("rock.com");
        authorUpdateDTO.setAddress(new AddressUpdateDTO("rua c", 333, "caucaia", "CE", "627000003"));

        return authorUpdateDTO;
    }
}
