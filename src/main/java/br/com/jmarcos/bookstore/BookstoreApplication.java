package br.com.jmarcos.bookstore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import br.com.jmarcos.bookstore.model.Address;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.service.AuthorService;
import br.com.jmarcos.bookstore.service.BookService;
import br.com.jmarcos.bookstore.service.PermissionService;
import br.com.jmarcos.bookstore.service.PersonService;
import br.com.jmarcos.bookstore.service.PublishingCompanyService;
import br.com.jmarcos.bookstore.service.StorehouseService;

@EnableSpringDataWebSupport
@SpringBootApplication
@EnableCaching
public class BookstoreApplication implements CommandLineRunner {

	private final AuthorService authorService;
	private final BookService bookService;
	private final PublishingCompanyService publhService;
	private final StorehouseService storehouseService;
	private final PermissionService permissionService;
	private final PersonService personService;

	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

	public BookstoreApplication(AuthorService authorService,
			BookService bookService,
			PublishingCompanyService publhService,
			StorehouseService storehouseService,
			PermissionService permissionService,
			PersonService personService) {
		this.authorService = authorService;
		this.bookService = bookService;
		this.publhService = publhService;
		this.storehouseService = storehouseService;
		this.permissionService = permissionService;
		this.personService = personService;
	}

	@Override
	public void run(String... args) throws Exception {
		/*
		 * List<Permission> permissions = this.permissionService.search();
		 * List<Person> persons = this.personService.search();
		 * if (permissions.isEmpty()) {
		 * Permission permissionADM = new Permission("ROLE_ADMIN");
		 * Permission permissionUser = new Permission("ROLE_USER");
		 * this.permissionService.save(permissionADM);
		 * this.permissionService.save(permissionUser);
		 * }
		 * 
		 * if (persons.isEmpty()) {
		 * Person personADMIN = new Person();
		 * personADMIN.setEmail("ADMINADMIN");
		 * personADMIN.setName("ADMIN");
		 * personADMIN.setPassword(new BCryptPasswordEncoder().encode("ADMIN123"));
		 * personADMIN.getPermission().add(this.permissionService.searchByname(
		 * "ROLE_ADMIN").get());
		 * this.personService.save(personADMIN);
		 * 
		 * Person personUser = new Person();
		 * personUser.setEmail("user@gmail.com");
		 * personUser.setName("user");
		 * personUser.setPassword(new BCryptPasswordEncoder().encode("user123"));
		 * personUser.getPermission().add(this.permissionService.searchByname(
		 * "ROLE_USER").get());
		 * this.personService.save(personUser);
		 * 
		 * }
		 * Page<Author> pageResultAuthor = this.authorService.search(PageRequest.of(0,
		 * 10));
		 * List<Author> authorList = pageResultAuthor.getContent();
		 * 
		 * if (authorList.isEmpty()) {
		 * Author jk = new Author();
		 * jk.setName("J. K. Rowling");
		 * jk.setUrl("JoanneRowling.com");
		 * jk.setAddress(new Address("Rua A", 000, "Edimburgo", "Escócia",
		 * "00000-000"));
		 * this.authorService.save(jk);
		 * for (int i = 1; i <= 10; i++) {
		 * Address address = new Address("Rua das Flores", 1234, "São Paulo", "SP" + i,
		 * "01234-567");
		 * Author author = new Author();
		 * author.setName("Autor " + i);
		 * author.setUrl("autor alguma coisa" + i);
		 * author.setAddress(address);
		 * authorService.save(author);
		 * }
		 * }
		 * 
		 * Page<PublishingCompany> pageResultPublishingCompany =
		 * this.publhService.search(PageRequest.of(0, 10));
		 * List<PublishingCompany> publishingCompanyList =
		 * pageResultPublishingCompany.getContent();
		 * if (publishingCompanyList.isEmpty()) {
		 * PublishingCompany rocco = new PublishingCompany();
		 * rocco.setName("Rocco");
		 * rocco.setPhone("(21) 3525-2000");
		 * rocco.setAddress(new Address("Rua do Passeio", 38, "Rio de Janeiro", "RJ",
		 * "	20000000"));
		 * rocco.setUrl("www.rocco.com.br");
		 * this.publhService.save(rocco);
		 * 
		 * for (int i = 1; i <= 10; i++) {
		 * Address address = new Address("Rua das dores" + i, 1234, "São Paulo", "SP",
		 * "01234-567");
		 * PublishingCompany publishingCompany = new PublishingCompany();
		 * publishingCompany.setName("Editora " + i);
		 * publishingCompany.setUrl("editora" + i + "@exemplo.com");
		 * publishingCompany.setAddress(address);
		 * publishingCompany.setPhone("(11) 1234-567" + i);
		 * publhService.save(publishingCompany);
		 * }
		 * }
		 * 
		 * Page<Storehouse> pageResulStorehouse =
		 * this.storehouseService.search(PageRequest.of(0, 10));
		 * List<Storehouse> storehouseList = pageResulStorehouse.getContent();
		 * if (storehouseList.isEmpty()) {
		 * 
		 * for (int i = 1; i <= 10; i++) {
		 * Address address = new Address("Rua das freiras", 1234, "São Paulo" + i, "SP",
		 * "01234-567");
		 * Storehouse storehouse = new Storehouse();
		 * storehouse.setCode(i);
		 * storehouse.setAddress(address);
		 * storehouse.setPhone("(11) 1234-567" + i);
		 * storehouseService.save(storehouse);
		 * }
		 * 
		 * Page<Book> pageResulBook = this.bookService.search(PageRequest.of(0, 10));
		 * List<Book> bookList = pageResulBook.getContent();
		 * Integer cem = 100;
		 * List<Integer> quantites = new ArrayList();
		 * for (int i = 1; i <= 10; i++) {
		 * quantites.add(cem);
		 * }
		 * 
		 * if (bookList.isEmpty()) {
		 * 
		 * Book harryPotter1 = new Book();
		 * harryPotter1.getAuthorList().add(this.authorService.
		 * searchByName("J. K. Rowling").get());
		 * harryPotter1.
		 * setTitle("Harry Potter e a pedra filosofal (specitial edition from 20 years)"
		 * );
		 * harryPotter1.setPrice(new BigDecimal(50.00));
		 * harryPotter1.setYear(2020);
		 * harryPotter1.setPublishingCompany(this.publhService.searchByName("Rocco").get
		 * ());
		 * for (int i = 1; i <= 10; i++) {
		 * harryPotter1.getStorehouseList().add(this.storehouseService.searchByCode(i).
		 * get());
		 * }
		 * this.bookService.save(harryPotter1, quantites);
		 * 
		 * Book harryPotter2 = new Book();
		 * harryPotter2.getAuthorList().add(this.authorService.
		 * searchByName("J. K. Rowling").get());
		 * harryPotter2.
		 * setTitle("Harry Potter e a camara secreta (specitial edition from 20 years)"
		 * );
		 * harryPotter2.setPrice(new BigDecimal(50.00));
		 * harryPotter2.setYear(2020);
		 * harryPotter2.setPublishingCompany(this.publhService.searchByName("Rocco").get
		 * ());
		 * for (int i = 1; i <= 10; i++) {
		 * harryPotter2.getStorehouseList().add(this.storehouseService.searchByCode(i).
		 * get());
		 * }
		 * this.bookService.save(harryPotter2, quantites);
		 * 
		 * Book harryPotter3 = new Book();
		 * harryPotter3.getAuthorList().add(this.authorService.
		 * searchByName("J. K. Rowling").get());
		 * harryPotter3.
		 * setTitle("Harry Potter e o Prisioneiro de Azkaban (specitial edition from 20 years)"
		 * );
		 * harryPotter3.setPrice(new BigDecimal(50.00));
		 * harryPotter3.setYear(2020);
		 * harryPotter3.setPublishingCompany(this.publhService.searchByName("Rocco").get
		 * ());
		 * for (int i = 1; i <= 10; i++) {
		 * harryPotter3.getStorehouseList().add(this.storehouseService.searchByCode(i).
		 * get());
		 * }
		 * this.bookService.save(harryPotter3, quantites);
		 * 
		 * Book harryPotter4 = new Book();
		 * harryPotter4.getAuthorList().add(this.authorService.
		 * searchByName("J. K. Rowling").get());
		 * harryPotter4.
		 * setTitle("Harry Potter e o Calice de fogo (specitial edition from 20 years)"
		 * );
		 * harryPotter4.setPrice(new BigDecimal(50.00));
		 * harryPotter4.setYear(2020);
		 * harryPotter4.setPublishingCompany(this.publhService.searchByName("Rocco").get
		 * ());
		 * for (int i = 1; i <= 10; i++) {
		 * harryPotter4.getStorehouseList().add(this.storehouseService.searchByCode(i).
		 * get());
		 * }
		 * this.bookService.save(harryPotter4, quantites);
		 * 
		 * Book harryPotter5 = new Book();
		 * harryPotter5.getAuthorList().add(this.authorService.
		 * searchByName("J. K. Rowling").get());
		 * harryPotter5.
		 * setTitle("Harry Potter e a Ordem da Fenix (specitial edition from 20 years)"
		 * );
		 * harryPotter5.setPrice(new BigDecimal(50.00));
		 * harryPotter5.setYear(2020);
		 * harryPotter5.setPublishingCompany(this.publhService.searchByName("Rocco").get
		 * ());
		 * for (int i = 1; i <= 10; i++) {
		 * harryPotter5.getStorehouseList().add(this.storehouseService.searchByCode(i).
		 * get());
		 * }
		 * this.bookService.save(harryPotter5, quantites);
		 * 
		 * Book harryPotter6 = new Book();
		 * harryPotter6.getAuthorList().add(this.authorService.
		 * searchByName("J. K. Rowling").get());
		 * harryPotter6.
		 * setTitle("Harry Potter e o Enigma do principe (specitial edition from 20 years)"
		 * );
		 * harryPotter6.setPrice(new BigDecimal(50.00));
		 * harryPotter6.setYear(2020);
		 * harryPotter6.setPublishingCompany(this.publhService.searchByName("Rocco").get
		 * ());
		 * for (int i = 1; i <= 10; i++) {
		 * harryPotter6.getStorehouseList().add(this.storehouseService.searchByCode(i).
		 * get());
		 * }
		 * this.bookService.save(harryPotter6, quantites);
		 * 
		 * Book harryPotter7 = new Book();
		 * harryPotter7.getAuthorList().add(this.authorService.
		 * searchByName("J. K. Rowling").get());
		 * harryPotter7.
		 * setTitle("Harry Potter e as Reliquias da Morte (specitial edition from 20 years)"
		 * );
		 * harryPotter7.setPrice(new BigDecimal(50.00));
		 * harryPotter7.setYear(2020);
		 * harryPotter7.setPublishingCompany(this.publhService.searchByName("Rocco").get
		 * ());
		 * for (int i = 1; i <= 10; i++) {
		 * harryPotter7.getStorehouseList().add(this.storehouseService.searchByCode(i).
		 * get());
		 * }
		 * this.bookService.save(harryPotter7, quantites);
		 * 
		 * for (int i = 1; i <= 10; i++) {
		 * Book book = new Book();
		 * String authorName = "Autor " + i;
		 * book.getAuthorList().add(this.authorService.searchByName(authorName).get());
		 * book.setTitle("Book " + i);
		 * book.setPrice(new BigDecimal(50.00));
		 * book.setYear(2020);
		 * book.setPublishingCompany(this.publhService.searchByName("Rocco").get());
		 * for (int index = 1; index <= 10; index++) {
		 * book.getStorehouseList().add(this.storehouseService.searchByCode(index).get()
		 * );
		 * }
		 * this.bookService.save(book, quantites);
		 * }
		 * 
		 * }
		 * 
		 * }
		 */

	}
}
