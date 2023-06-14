package br.com.jmarcos.bookstore.person;

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
import br.com.jmarcos.bookstore.controller.dto.person.PersonUpdateDTO;
import br.com.jmarcos.bookstore.model.Address;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.repository.PersonRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.GroceryCartBookRepository;
import br.com.jmarcos.bookstore.service.PermissionService;
import br.com.jmarcos.bookstore.service.PersonService;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PermissionService permissionService;
    
    @Mock
    private GroceryCartBookRepository groceryCartBookRepository;


    @Test
    void test() {
        Assertions.assertTrue(true);
    }

    @Test
    void search_returns_AllPersons_WhenSuccessful() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Person> personList = List.of(createPerson());
        PageImpl<Person> personPage = new PageImpl<>(personList);
        when(personRepository.findAll(pageable)).thenReturn(personPage);

        Page<Person> all = personService.search(pageable);
        List<Person> returnedPersonList = all.stream().toList();

        Assertions.assertFalse(returnedPersonList.isEmpty());
        Assertions.assertNotNull(returnedPersonList.get(0).getId());
        Assertions.assertEquals(personList.get(0).getName(), returnedPersonList.get(0).getName());
        Assertions.assertEquals(personList.get(0).getPhone(), returnedPersonList.get(0).getPhone());
        Assertions.assertEquals(personList.get(0).getEmail(), returnedPersonList.get(0).getEmail());
        Assertions.assertEquals(personList.get(0).getAddress(), returnedPersonList.get(0).getAddress());
        Assertions.assertEquals(personList.get(0).getGroceryCarts(), returnedPersonList.get(0).getGroceryCarts());
        Assertions.assertEquals(personList.get(0).getPassword(), returnedPersonList.get(0).getPassword());

        verify(personRepository).findAll(pageable);

    }

    @Test
    void save_returns_ASavedPerson_WhenSuccessful() {
        Person person = createPerson();
        when(personRepository.save(any(Person.class))).thenReturn(person);

        Person savedPerson = personService.save(person);

        Assertions.assertNotNull(savedPerson.getId());
        Assertions.assertEquals(person.getName(), savedPerson.getName());
        Assertions.assertEquals(person.getPhone(), savedPerson.getPhone());
        Assertions.assertEquals(person.getEmail(), savedPerson.getEmail());
        Assertions.assertEquals(person.getAddress(), savedPerson.getAddress());
        Assertions.assertEquals(person.getGroceryCarts(), savedPerson.getGroceryCarts());
        Assertions.assertEquals(person.getPassword(), savedPerson.getPassword());
        Assertions.assertTrue(savedPerson.getPermission().isEmpty());

        verify(personRepository).save(person);

    }

    @Test
    void save_Throws_ConflictException_WhenPersonEmailIsAlreadyInUse() {
        Person person = createPerson();
        Person newPerson = createPerson();
        when(personRepository.findByEmail(anyString())).thenReturn(Optional.of(person));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> personService.save(newPerson));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("Email is already in use"));

    }

    @Test
    void searchById_returns_APersonTheGivenId_WhenSuccessful() {
        Person person = createPerson();
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        Person returnedPerson = this.personService.searchById(person.getId());

        Assertions.assertNotNull(returnedPerson);
        Assertions.assertNotNull(returnedPerson.getId());
        Assertions.assertEquals(person.getName(), returnedPerson.getName());
        Assertions.assertEquals(person.getPhone(), returnedPerson.getPhone());
        Assertions.assertEquals(person.getEmail(), returnedPerson.getEmail());
        Assertions.assertEquals(person.getAddress(), returnedPerson.getAddress());
        Assertions.assertEquals(person.getGroceryCarts(), returnedPerson.getGroceryCarts());
        Assertions.assertEquals(person.getPassword(), returnedPerson.getPassword());
        Assertions.assertTrue(person.getPermission().isEmpty());

        verify(personRepository).findById(person.getId());
    }


    @Test
    void searchById_Throws_ResourceNotFoundException_WhenPersonNotFound() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> personService.searchById(1L));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Person not found with the given id"));
        
    }

    @Test
    void searchByEmail_returns_APersonWithTheGivenEmail_WhenSuccessful() {
        Person person = createPerson();
        when(personRepository.findByEmail(person.getEmail())).thenReturn(Optional.of(person));

        Person returnedPerson = this.personService.searchByEmail(person.getEmail());

        Assertions.assertNotNull(returnedPerson);
        Assertions.assertNotNull(returnedPerson.getId());
        Assertions.assertEquals(person.getName(), returnedPerson.getName());
        Assertions.assertEquals(person.getPhone(), returnedPerson.getPhone());
        Assertions.assertEquals(person.getEmail(), returnedPerson.getEmail());
        Assertions.assertEquals(person.getAddress(), returnedPerson.getAddress());
        Assertions.assertEquals(person.getGroceryCarts(), returnedPerson.getGroceryCarts());
        Assertions.assertEquals(person.getPassword(), returnedPerson.getPassword());
        Assertions.assertTrue(person.getPermission().isEmpty());

        verify(personRepository).findByEmail(person.getEmail());
    }

    @Test
    void searchByEmail_Throws_ResourceNotFoundException_WhenPersonNotFound() {
        when(personRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> personService.searchByEmail(anyString()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Person not found with the given email"));
        
    }

    @Test
    void deleteById_deletesAPerson_WhenSuccessful() {
        Person person = createPerson();
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(person));


        this.personService.deleteById(person.getId());

        verify(personRepository).deleteById(person.getId());
        verify(groceryCartBookRepository).deleteAllByGroceryCartId(anyLong());
        verify(personRepository).findById(person.getId());
    }

    @Test
    void deleteById_Throws_ResourceNotFoundException_WhenPersonNotFound() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> personService.deleteById(anyLong()));


            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Person not found with the given id"));
        
    }

    @Test
    void update_returns_AnUpdatedPerson_WhenSuccessful() {
        Person person = createPerson();
        PersonUpdateDTO personUpdateDTO = createPersonUpdateDTO();
        when(personRepository.save(person)).thenReturn(person);
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(person));

        Person updatedPerson = personService.update(personUpdateDTO.toPerson(1L));

        Assertions.assertNotNull(updatedPerson);
        Assertions.assertNotNull(updatedPerson.getId());
        Assertions.assertEquals(person.getName(), updatedPerson.getName());
        Assertions.assertEquals(person.getPhone(), updatedPerson.getPhone());
        Assertions.assertEquals(person.getEmail(), updatedPerson.getEmail());
        Assertions.assertEquals(person.getAddress(), updatedPerson.getAddress());
        Assertions.assertEquals(person.getGroceryCarts(), updatedPerson.getGroceryCarts());
        Assertions.assertEquals(person.getPassword(), updatedPerson.getPassword());
        Assertions.assertTrue(person.getPermission().isEmpty());

        verify(personRepository).save(person);

    }

    @Test
    void update_Throws_ConflictException_WhenPersonEmailIsAlreadyInUse() {
        Person person = createPerson();
        PersonUpdateDTO personUpdateDTO = createPersonUpdateDTO();
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(person));
        when(personRepository.findByEmail(anyString())).thenReturn(Optional.of(person));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> personService.update(personUpdateDTO.toPerson(1L)));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("email is already in use."));

    }

    @Test
    void addPermission_AddApermissionToAPerson_WhenSuccessful() {
        Person person = createPerson();
        Permission permission = createPermission();
        when(permissionService.searchByName(anyString())).thenReturn(permission);

        this.personService.addPermission(person, permission.getName());

        Assertions.assertFalse(person.getPermission().isEmpty());
        Assertions.assertEquals(person.getPermission().get(0).getName(), permission.getName());
        verify(permissionService).searchByName(permission.getName());
        verify(personRepository).save(person);

    }


    public Person createPerson() {
        Person person = new Person();

        person.setId(1L);
        person.setName("Antonio");
        person.setPhone("99 9999-9999");
        person.setEmail("antonio@gmail.com");
        person.setAddress(new Address("rua b", 222, "fortaleza", "CE", "627000002"));
        person.getAddress().setId(1L);
        person.setGroceryCarts(createGroceryCartList());
        person.setPassword("123");

        return person;
    }

    public PersonUpdateDTO createPersonUpdateDTO() {
        PersonUpdateDTO person = new PersonUpdateDTO();

        person.setName("Bernardo");
        person.setPhone("11 1111-1111");
        person.setEmail("bernardo@gmail.com");
        person.setAddress(new AddressUpdateDTO("rua c", 222, "um lugar ai", "CE", "627000003"));

        return person;
    }

    public List<GroceryCart> createGroceryCartList() {
        List<GroceryCart> groceryCarts = new ArrayList<>();
        GroceryCart groceryCart = new GroceryCart();

        groceryCart.setId(1L);
        groceryCart.getBooks().add(create());
        groceryCarts.add(groceryCart);
        return groceryCarts;

    }

    public Book create() {
    Book book = new Book();

    book.setId(1L);

    return book;

    }

    public Permission createPermission(){
        Permission permission = new Permission();

        permission.setId(1L);
        permission.setName("USER");

        return permission;
    }
}
