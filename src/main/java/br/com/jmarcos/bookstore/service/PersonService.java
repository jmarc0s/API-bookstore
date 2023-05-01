package br.com.jmarcos.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.GroceryCart;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.repository.GroceryCartRepository;
import br.com.jmarcos.bookstore.repository.PermissionRepository;
import br.com.jmarcos.bookstore.repository.PersonRepository;
import br.com.jmarcos.bookstore.repository.intermediateClass.GroceryCartBookRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PermissionRepository perissionRepository;
    private final GroceryCartBookRepository groceryCartBookRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, PermissionRepository perissionRepository,
            GroceryCartBookRepository groceryCartBookRepository) {
        this.personRepository = personRepository;
        this.perissionRepository = perissionRepository;
        this.groceryCartBookRepository = groceryCartBookRepository;
    }

    public boolean existsByEmail(String email) {
        Optional<Person> exist = this.personRepository.findByEmail(email);
        if (exist.isPresent())
            return true;
        return false;
    }

    public Person save(Person person) {
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

    private List<Permission> findPermissions(List<Permission> permissions) {
        List<Permission> permissionsList = new ArrayList<>();

        for (Permission permission : permissions) {
            Optional<Permission> permissionExist = this.perissionRepository.findByName(permission.getName());
            permissionsList.add(permissionExist.get());

        }
        return permissionsList;
    }

    public Optional<Person> searchById(Long id) {
        return this.personRepository.findById(id);
    }

    public Optional<Person> searchByEmail(String email) {
        return this.personRepository.findByEmail(email);
    }

    @Transactional
    public boolean deleteByid(Long id) {
        Optional<Person> person = this.personRepository.findById(id);
        if (person.isPresent()) {
            this.deleteGroceryCartBooks(person.get());
            this.personRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void deleteGroceryCartBooks(Person person) {
        List<GroceryCart> groceryCarts = person.getGroceryCarts();
        for (GroceryCart groceryCart : groceryCarts) {
            this.groceryCartBookRepository.deleteAllByGroceryCartId(groceryCart.getId());
        }

    }

    public Optional<Person> addPermission(Person person, String permission) {
        Optional<Permission> permissionExist = this.perissionRepository.findByName(permission);
        if (permissionExist.isPresent()) {
            person.getPermission().add(permissionExist.get());
            return Optional.of(this.personRepository.save(person));
        }

        return Optional.empty();
    }

    public Optional<Person> update(Person personInDataBase, Person newPerson) {
        personInDataBase.setName(newPerson.getName());
        personInDataBase.setEmail(newPerson.getEmail());
        personInDataBase.setAddress(newPerson.getAddress());
        personInDataBase.setPhone(newPerson.getPhone());
        return Optional.of(this.personRepository.save(personInDataBase));
    }

}
