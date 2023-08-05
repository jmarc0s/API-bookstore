package br.com.jmarcos.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.repository.PersonRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.GroceryCartBookRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PermissionService permissionService;
    private final GroceryCartBookRepository groceryCartBookRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public PersonService(PersonRepository personRepository, PermissionService permissionService,
            GroceryCartBookRepository groceryCartBookRepository, JavaMailSender mailSender) {
        this.personRepository = personRepository;
        this.permissionService = permissionService;
        this.groceryCartBookRepository = groceryCartBookRepository;
        this.mailSender = mailSender;
    }

    public boolean existsByEmail(String email) {
        Optional<Person> exist = this.personRepository.findByEmail(email);
        return exist.isPresent();
    }

    public Person save(Person person) {
        if (this.existsByEmail(person.getEmail())) {
            throw new ConflictException("Email is already in use");
        }

        List<Permission> permissions = this.findPermissions(person.getPermission());
        this.sendConfirmationEmail(person);
        person.setPermission(permissions);
        person.setConfirmationCode(this.generateConfirmationCode());
        return this.personRepository.save(person);
    }

    public Page<Person> search(Pageable pageable) {
        return this.personRepository.findAll(pageable);
    }

    public List<Person> search() {
        return this.personRepository.findAll();
    }

    public Person searchById(Long id) {
        return this.personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with the given id"));
    }

    public Person searchByEmail(String email) {
        return this.personRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with the given email"));
    }

    @Transactional
    public void deleteById(Long id) {
        Person person = this.searchById(id);

        this.deleteGroceryCartBooks(person);
        this.personRepository.deleteById(id);
    }

    public Person update(Person newPerson) {
        Person personInDataBase = this.searchById(newPerson.getId());

        if (!Objects.equals(personInDataBase.getEmail(), newPerson.getEmail())
                && this.existsByEmail(newPerson.getEmail())) {
            throw new ConflictException("email is already in use.");

        }

        Person updatedPerson = this.fillUpdatePerson(personInDataBase, newPerson);

        return this.personRepository.save(updatedPerson);
    }

    public Person addPermission(Person person, String permission) {
        Permission permissionExist = this.permissionService.searchByName(permission);

        person.getPermission().add(permissionExist);

        return this.personRepository.save(person);

    }

    public Person fillUpdatePerson(Person personInDataBase, Person newPerson) {
        personInDataBase.setName(newPerson.getName());
        personInDataBase.setEmail(newPerson.getEmail());
        personInDataBase.setAddress(newPerson.getAddress());
        personInDataBase.setPhone(newPerson.getPhone());

        return personInDataBase;
    }

    private List<Permission> findPermissions(List<Permission> permissions) {
        List<Permission> permissionsList = new ArrayList<>();

        for (Permission permission : permissions) {
            Permission permissionExist = this.permissionService.searchByName(permission.getName());
            permissionsList.add(permissionExist);

        }
        return permissionsList;
    }

    private void deleteGroceryCartBooks(Person person) {
        List<GroceryCart> groceryCarts = person.getGroceryCarts();
        for (GroceryCart groceryCart : groceryCarts) {
            this.groceryCartBookRepository.deleteAllByGroceryCartId(groceryCart.getId());
        }

    }

    public void sendConfirmationEmail(Person person) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(person.getEmail());
        message.setSubject("Codigo de verificação");
        message.setText("Olá! Bem vindo a ApiBookstore. Seu codigo de verificação é: " + person.getConfirmationCode());
        mailSender.send(message);
    }

    private Integer generateConfirmationCode() {
        //create a logic to create a confirmation code
        return 1234;
    }

    public String confirmCode(String email, Integer code) {
        Person person = this.searchByEmail(email);
        if(!Objects.equals(person.getConfirmationCode(), code)){
            //throws an exception telling that this is an invalid code
        }

        person.setAccountNonLocked(true);
        person.setConfirmationCode(null);
        this.personRepository.save(person);

        return "now you're free to log in";
    }

}
