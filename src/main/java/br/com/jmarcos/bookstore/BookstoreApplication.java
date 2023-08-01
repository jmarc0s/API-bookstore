package br.com.jmarcos.bookstore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.jmarcos.bookstore.controller.dto.author.AuthorRequestDTO;
import br.com.jmarcos.bookstore.model.Address;
import br.com.jmarcos.bookstore.model.Author;
import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.Permission;
import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.model.enums.BookCategory;
import br.com.jmarcos.bookstore.model.intermediateClass.StorehouseBook;
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

		List<Permission> permissions = this.permissionService.search();
		List<Person> persons = this.personService.search();

		if (permissions.isEmpty()) {
			Permission permissionADM = new Permission("ROLE_ADMIN");
			Permission permissionUser = new Permission("ROLE_USER");
			this.permissionService.save(permissionADM);
			this.permissionService.save(permissionUser);
		}

		if (persons.isEmpty()) {
			Person personADMIN = new Person();
			personADMIN.setEmail("ADMINADMIN");
			personADMIN.setName("ADMIN");
			personADMIN.setPassword(new BCryptPasswordEncoder().encode("ADMIN123"));
			personADMIN.getPermission().add(this.permissionService.searchByName(
					"ROLE_ADMIN"));
			this.personService.save(personADMIN);

			Person personUser = new Person();
			personUser.setEmail("user@gmail.com");
			personUser.setName("user");
			personUser.setPassword(new BCryptPasswordEncoder().encode("user123"));
			personUser.getPermission().add(this.permissionService.searchByName(
					"ROLE_USER"));
			this.personService.save(personUser);

		}
		Page<Author> pageResultAuthor = this.authorService.search(PageRequest.of(0, 10));

		if (pageResultAuthor.isEmpty()) {
			for (int i = 1; i <= 10; i++) {
				Address address = new Address("Rua das Flores", 1234, "São Paulo", "SP" + i,
						"01234-567");
				Author author = new Author();
				author.setName("Autor " + i);
				author.setUrl("autor alguma coisa" + i + ".com");
				author.setAddress(address);
				authorService.save(author);

			}

		}

		Page<PublishingCompany> pageResultPublishingCompany = this.publhService.search(PageRequest.of(0, 10));

		if (pageResultPublishingCompany.isEmpty()) {
			for (int i = 1; i <= 10; i++) {
				Address address = new Address("Rua das dores" + i, 1234, "São Paulo", "SP",
						"01234-567");
				PublishingCompany publishingCompany = new PublishingCompany();
				publishingCompany.setName("Editora " + i);
				publishingCompany.setUrl("editora" + i + "@exemplo.com");
				publishingCompany.setAddress(address);
				publishingCompany.setPhone("(11) 1234-567" + i);
				publhService.save(publishingCompany);

			}
		}

		Page<Storehouse> pageResulStorehouse = this.storehouseService.search(PageRequest.of(0, 10));
		if (pageResulStorehouse.isEmpty()) {

			for (int i = 1; i <= 10; i++) {
				Address address = new Address("Rua das freiras", 1234, "São Paulo" + i, "SP",
						"01234-567");
				Storehouse storehouse = new Storehouse();
				storehouse.setCode(i);
				storehouse.setAddress(address);
				storehouse.setPhone("(11) 1234-567" + i);
				storehouseService.save(storehouse);

			}

			Page<Book> pageResulBook = this.bookService.search(PageRequest.of(0, 10), null, null, null);
			Integer cem = 100;
			List<Integer> quantites = new ArrayList<>();
			for (int i = 1; i <= 10; i++) {
				quantites.add(cem);
			}

			if (pageResulBook.isEmpty()) {
				List<BookCategory> allCategories = List.of(BookCategory.BIBLIOGRAPHY,
						BookCategory.CLASSICLITERATURE, BookCategory.FICTION,
						BookCategory.MYSTERY, BookCategory.ROMANCE,
						BookCategory.SCIENCEFICTION, BookCategory.TECHNOLOGY);
				for (int i = 1; i <= 10; i++) {
					int quantityOfCategoriesInABook = ThreadLocalRandom.current().nextInt(1, allCategories.size() + 1);
					Set<BookCategory> categories = new HashSet<>();
					List<StorehouseBook> storehouseBooks = new ArrayList<>();
					Book book = new Book();
					book.getAuthorList().add(AuthorRequestDTO.toAuthor(Long.valueOf(i)));
					book.setTitle("Book " + i);
					book.setPrice(new BigDecimal(50.00));
					book.setYear(2020 - i);
					book.setPublishingCompany(new PublishingCompany(Long.valueOf(i)));

					for (int ind = 0; ind < quantityOfCategoriesInABook; ind++) {
						int categoryIndex = ThreadLocalRandom.current().nextInt(0, allCategories.size());
						categories.add(allCategories.get(categoryIndex));
					}

					book.setCategories(categories);
					for (int index = 1; index <= 10; index++) {

						StorehouseBook storehouseBook = new StorehouseBook();
						storehouseBook.setQuantity(100);
						storehouseBook.setStorehouse(this.storehouseService.searchByCode(index));
						book.getStorehouseList().add(storehouseBook.getStorehouse());

						storehouseBooks.add(storehouseBook);
					}

					this.bookService.save(book, storehouseBooks);
				}

			}

		}

	}
}
