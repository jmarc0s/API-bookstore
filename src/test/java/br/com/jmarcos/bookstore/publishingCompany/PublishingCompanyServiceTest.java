package br.com.jmarcos.bookstore.publishingCompany;

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
import br.com.jmarcos.bookstore.controller.dto.publishinCompany.PublishingCompanyUpdateDTO;
import br.com.jmarcos.bookstore.model.Address;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.repository.PublishingCompanyRepository;
import br.com.jmarcos.bookstore.service.BookService;
import br.com.jmarcos.bookstore.service.PublishingCompanyService;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PublishingCompanyServiceTest {

    @InjectMocks
    private PublishingCompanyService publishingCompanyService;

    @Mock
    private PublishingCompanyRepository publishingCompanyRepository;

    @Mock
    private BookService bookService;

    @Test
    void test() {
        Assertions.assertTrue(true);
    }

    @Test
    void search_returns_AllPublishingCompanies_WhenSuccessful() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<PublishingCompany> publishingCompanyList = List.of(createPublishingCompany());
        PageImpl<PublishingCompany> publishingCompanyPage = new PageImpl<>(publishingCompanyList);

        when(publishingCompanyRepository.findAll(pageable)).thenReturn(publishingCompanyPage);

        Page<PublishingCompany> all = publishingCompanyService.search(pageable);
        List<PublishingCompany> publishingCompanysSavedList = all.stream().toList();

        Assertions.assertFalse(publishingCompanysSavedList.isEmpty());
        Assertions.assertEquals(publishingCompanyList.get(0).getPhone(), publishingCompanysSavedList.get(0).getPhone());
        Assertions.assertEquals(publishingCompanyList.get(0).getName(), publishingCompanysSavedList.get(0).getName());
        Assertions.assertEquals(publishingCompanyList.get(0).getUrl(), publishingCompanysSavedList.get(0).getUrl());
        Assertions.assertEquals(publishingCompanyList.get(0).getAddress(),
                publishingCompanysSavedList.get(0).getAddress());
        Assertions.assertEquals(publishingCompanyList.get(0).getBookList(),
                publishingCompanysSavedList.get(0).getBookList());
        Assertions.assertNotNull(publishingCompanysSavedList.get(0).getId());

        verify(publishingCompanyRepository).findAll(pageable);

    }

    @Test
    void save_returns_ASavedPublishingCompany_WhenSuccessful() {
        PublishingCompany publishingCompany = createPublishingCompany();
        when(publishingCompanyRepository.save(any(PublishingCompany.class))).thenReturn(publishingCompany);

        PublishingCompany savedPublishingCompany = publishingCompanyService.save(publishingCompany);

        Assertions.assertNotNull(savedPublishingCompany);
        Assertions.assertNotNull(savedPublishingCompany.getId());
        Assertions.assertEquals(publishingCompany.getName(), savedPublishingCompany.getName());
        Assertions.assertEquals(publishingCompany.getPhone(), savedPublishingCompany.getPhone());
        Assertions.assertEquals(publishingCompany.getAddress(), savedPublishingCompany.getAddress());
        Assertions.assertEquals(publishingCompany.getUrl(), savedPublishingCompany.getUrl());
        Assertions.assertEquals(publishingCompany.getBookList(), savedPublishingCompany.getBookList());
        verify(publishingCompanyRepository).save(publishingCompany);

    }

    @Test
    void save_Throws_ConflictException_WhenPublisshingCompanyNameIsAlreadyInUse() {
        PublishingCompany publishingCompany = createPublishingCompany();
        PublishingCompany newPublishingCompany = createPublishingCompany();
        when(publishingCompanyRepository.findByName(anyString())).thenReturn(Optional.of(publishingCompany));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> publishingCompanyService.save(newPublishingCompany));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("PublishingCompany name is already in use"));

    }

    @Test
    void searchById_returns_APublishingCompanyTheGivenId_WhenSuccessful() {
        PublishingCompany publishingCompany = createPublishingCompany();
        when(publishingCompanyRepository.findById(publishingCompany.getId()))
                .thenReturn(Optional.of(publishingCompany));

        PublishingCompany returnedPublishingCompany = this.publishingCompanyService
                .searchById(publishingCompany.getId());

        Assertions.assertNotNull(returnedPublishingCompany);
        Assertions.assertNotNull(returnedPublishingCompany.getId());
        Assertions.assertEquals(publishingCompany.getName(), returnedPublishingCompany.getName());
        Assertions.assertEquals(publishingCompany.getPhone(), returnedPublishingCompany.getPhone());
        Assertions.assertEquals(publishingCompany.getAddress(), returnedPublishingCompany.getAddress());
        Assertions.assertEquals(publishingCompany.getUrl(), returnedPublishingCompany.getUrl());
        Assertions.assertEquals(publishingCompany.getBookList(), returnedPublishingCompany.getBookList());
        verify(publishingCompanyRepository).findById(publishingCompany.getId());
    }

    @Test
    void searchById_Throws_ResourceNotFoundException_WhenPublishingCompanyNotFound() {
        when(publishingCompanyRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> publishingCompanyService.searchById(1L));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("PublishingCompany not found with the given id"));
        
    }

    @Test
    void searchByName_returns_APublishingCompanyTheGivenName_WhenSuccessful() {
        PublishingCompany publishingCompany = createPublishingCompany();
        when(publishingCompanyRepository.findByName(publishingCompany.getName()))
                .thenReturn(Optional.of(publishingCompany));

        PublishingCompany returnedPublishingCompany = this.publishingCompanyService
                .searchByName(publishingCompany.getName());

        Assertions.assertNotNull(returnedPublishingCompany);
        Assertions.assertNotNull(returnedPublishingCompany.getId());
        Assertions.assertEquals(publishingCompany.getName(), returnedPublishingCompany.getName());
        Assertions.assertEquals(publishingCompany.getPhone(), returnedPublishingCompany.getPhone());
        Assertions.assertEquals(publishingCompany.getAddress(), returnedPublishingCompany.getAddress());
        Assertions.assertEquals(publishingCompany.getUrl(), returnedPublishingCompany.getUrl());
        Assertions.assertEquals(publishingCompany.getBookList(), returnedPublishingCompany.getBookList());
        verify(publishingCompanyRepository).findByName(publishingCompany.getName());
    }

    @Test
    void searchByName_Throws_ResourceNotFoundException_WhenPublishingCompanyNotFound() {
        when(publishingCompanyRepository.findByName(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> publishingCompanyService.searchByName(anyString()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("PublishingCompany not found with the given name"));
        
    }

    @Test
    void delete_deletesAPublishingCompany_WhenSuccessful() {
        PublishingCompany publishingCompany = createPublishingCompany();
        when(publishingCompanyRepository.findById(anyLong())).thenReturn(Optional.of(publishingCompany));
        when(bookService.searchByPublishingCompany(anyLong())).thenReturn(publishingCompany.getBookList());

        this.publishingCompanyService.delete(publishingCompany.getId());

        verify(publishingCompanyRepository).delete(publishingCompany);
        verify(bookService).deleteStorehouseBook(anyLong());
        verify(publishingCompanyRepository).findById(publishingCompany.getId());
    }

    @Test
    void delete_Throws_ResourceNotFoundException_WhenPublishingCompanyNotFound() {
        when(publishingCompanyRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> publishingCompanyService.delete(anyLong()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("PublishingCompany not found with the given id"));
        
    }

    @Test
    void update_returns_AUpdatedPublishingCompany_WhenSuccessful() {
        PublishingCompany publishingCompany = createPublishingCompany();
        PublishingCompanyUpdateDTO publishingCompanyUpdateDTO = createPublishingCompanyUpdateDTO();
        when(publishingCompanyRepository.save(publishingCompany)).thenReturn(publishingCompany);
        when(publishingCompanyRepository.findById(anyLong())).thenReturn(Optional.of(publishingCompany));

        PublishingCompany updatedPublishingCompany = publishingCompanyService
                .update(publishingCompanyUpdateDTO.toPublishingCompany(1L), publishingCompany.getId());

        Assertions.assertNotNull(updatedPublishingCompany);
        Assertions.assertEquals(publishingCompany.getId(), updatedPublishingCompany.getId());
        Assertions.assertEquals(publishingCompany.getName(), updatedPublishingCompany.getName());
        Assertions.assertEquals(publishingCompany.getPhone(), updatedPublishingCompany.getPhone());
        Assertions.assertEquals(publishingCompany.getUrl(), updatedPublishingCompany.getUrl());
        Assertions.assertEquals(publishingCompany.getAddress(), updatedPublishingCompany.getAddress());
        verify(publishingCompanyRepository).save(publishingCompany);

    }

    @Test
    void update_Throws_ConflictException_WhenPublisshingCompanyNameIsAlreadyInUse() {
        PublishingCompany publishingCompany = createPublishingCompany();
        PublishingCompanyUpdateDTO publishingCompanyUpdateDTO = createPublishingCompanyUpdateDTO();
        when(publishingCompanyRepository.findById(anyLong())).thenReturn(Optional.of(publishingCompany));
        when(publishingCompanyRepository.findByName(anyString())).thenReturn(Optional.of(publishingCompany));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> publishingCompanyService.update(publishingCompanyUpdateDTO.toPublishingCompany(1L),
                                publishingCompany.getId()));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("PublishingCompany name is already in use"));

    }

    public PublishingCompany createPublishingCompany() {
        PublishingCompany publishingCompany = new PublishingCompany();

        publishingCompany.setId(1L);
        publishingCompany.setName("Rocco");
        publishingCompany.setPhone("99 9999-9999");
        publishingCompany.setUrl("Rocco.com");
        publishingCompany.setAddress(new Address("rua b", 222, "fortaleza", "CE", "627000002"));
        publishingCompany.getAddress().setId(1L);
        publishingCompany.setBookList(createBookList());

        return publishingCompany;
    }

    public List<Book> createBookList() {
        List<Book> bookList = new ArrayList<>();
        Book book = new Book();

        book.setId(1L);
        bookList.add(book);

        return bookList;

    }

    private PublishingCompanyUpdateDTO createPublishingCompanyUpdateDTO() {
        PublishingCompanyUpdateDTO publishingCompanyUpdateDTO = new PublishingCompanyUpdateDTO();

        publishingCompanyUpdateDTO.setPhone("11111111");
        publishingCompanyUpdateDTO.setName("rock");
        publishingCompanyUpdateDTO.setUrl("rock.com");
        publishingCompanyUpdateDTO.setAddress(new AddressUpdateDTO("rua c", 333, "caucaia", "CE", "627000003"));

        return publishingCompanyUpdateDTO;
    }
}
