package br.com.jmarcos.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

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
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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

    public void sendConfirmationEmail(String recipientEmail, String confirmationCode) {
        // final String username = "seu_email@gmail.com"; // Substitua pelo seu e-mail
        // final String password = "sua_senha"; // Substitua pela sua senha

        // // Configurações do servidor SMTP (no exemplo, usando o Gmail)
        // Properties props = new Properties();
        // props.put("mail.smtp.auth", "true");
        // props.put("mail.smtp.starttls.enable", "true");
        // props.put("mail.smtp.host", "smtp.gmail.com");
        // props.put("mail.smtp.port", "587");

        // Sessão de e-mail com autenticação
        // Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        // protected PasswordAuthentication getPasswordAuthentication() {
        // return new PasswordAuthentication(username, password);
        // }
        // });

        MimeMessage messageee = mailSender.createMimeMessage();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("null");
        message.setSubject("null");
        message.setText("null");
        mailSender.send(message);
        // try {
        // // Cria uma mensagem de e-mail
        // //Message message = new MimeMessage(session);
        // message.setFrom(new InternetAddress(username));
        // message.setRecipients(Message.RecipientType.TO,
        // InternetAddress.parse(recipientEmail));
        // message.setSubject("Confirmação de E-mail");
        // message.setText("Olá, obrigado por se cadastrar! Seu código de confirmação é:
        // " + confirmationCode);

        // // Envia o e-mail
        // Transport.send(message);

        // System.out.println("E-mail de confirmação enviado para: " + recipientEmail);
        // } catch (MessagingException e) {
        // System.out.println("Erro ao enviar o e-mail de confirmação: " +
        // e.getMessage());
        // }
    }

}
