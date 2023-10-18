package br.com.jmarcos.bookstore.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
import br.com.jmarcos.bookstore.service.exceptions.InvalidConfirmationCodeException;
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
        person.setPermission(permissions);
        person.setConfirmationCode(this.generateConfirmationCode());
        this.sendConfirmationEmail(person);
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

    private String generateConfirmationCode() {
        SecureRandom random = new SecureRandom();
        final int CODE_LENGTH = 6;
        Set<Character> selectedCharsForCode = new HashSet<>();

        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        String chars = "AB0CD1EF2GH3IJ4KL5MN6OP7QR8ST9UVWXYZ";

        while (selectedCharsForCode.size() < CODE_LENGTH){
            int index = random.nextInt(chars.length());
            char selectedChar = chars.charAt(index);
            
            if(!selectedCharsForCode.contains(selectedChar)){
                selectedCharsForCode.add(selectedChar); 
                sb.append(selectedChar);
            }
            
        }

        return sb.toString();
    }

    public String confirmCode(String email, String code) {
        Person person = this.searchByEmail(email);
        if (!Objects.equals(person.getConfirmationCode(), code)) {
            throw new InvalidConfirmationCodeException("Invalid Code!");
        }

        person.setAccountNonLocked(true);
        person.setConfirmationCode(null);
        this.personRepository.save(person);

        return "now you're free to log in";
    }

    public String changeEmailAndResendConfirmationCode(String oldEmail, String newEmail) {
        Person person = this.searchByEmail(oldEmail);

        if (this.existsByEmail(newEmail)) {
            throw new ConflictException("New email is already in use");
        }

        person.setEmail(newEmail);
        person.setConfirmationCode(this.generateConfirmationCode());

        this.sendConfirmationEmail(person);
        this.personRepository.save(person);

        return "Email changed";
    }

}
