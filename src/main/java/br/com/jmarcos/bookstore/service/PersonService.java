package br.com.jmarcos.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    public PersonService(PersonRepository personRepository, PermissionService permissionService,
            GroceryCartBookRepository groceryCartBookRepository) {
        this.personRepository = personRepository;
        this.permissionService = permissionService;
        this.groceryCartBookRepository = groceryCartBookRepository;
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
    public void deleteByid(Long id) {
        Person person = this.searchById(id);

        this.deleteGroceryCartBooks(person);
        this.personRepository.deleteById(id);
    }

    public Person update(Person personInDataBase, Person newPerson) {

        if (!Objects.equals(personInDataBase.getEmail(), newPerson.getEmail())
                && this.existsByEmail(newPerson.getEmail())) {
            throw new ResourceNotFoundException("email is already in use.");
        }

        Person updatedPerson = this.fillUpdatePerson(personInDataBase, newPerson);

        return this.personRepository.save(updatedPerson);
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
            Permission permissionExist = this.permissionService.searchByname(permission.getName());
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

    public Person addPermission(Person person, String permission) {
        Permission permissionExist = this.permissionService.searchByname(permission);

        person.getPermission().add(permissionExist);

        return this.personRepository.save(person);

    }

}
