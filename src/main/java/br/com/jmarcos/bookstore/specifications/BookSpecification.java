package br.com.jmarcos.bookstore.specifications;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.enums.BookCategory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class BookSpecification {

    public static Specification<Book> bookHasPriceLessThan(BigDecimal price) {

        if (price == null) {
            return null;
        }
        // return (root, criteriaQuery, criteriaBuilder) ->
        // criteriaBuilder.equal(root.get("price"), price );

        return new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.lessThan(root.get("price"), price);
            }
        };

    }

    public static Specification<Book> bookHasYear(Integer year) {

        // return (root, criteriaQuery, criteriaBuilder) ->
        // criteriaBuilder.equal(root.get("year"), year );

        if (year == null) {
            return null;
        }
        return new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("year"), year);
            }
        };

    }

    public static Specification<Book> bookHasCategories(List<BookCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<String> categorias = categories
                        .stream()
                        .map(t -> t.name()) // Converte o nome da categoria para o Enum correspondente
                        .collect(Collectors.toList());

                // Join<Object, Object> categoriesJoin = root.join("categories",
                // JoinType.INNER);
                // cb.in(null);
                // return /* root.get("categories").in(categorias) */ root.join("categories",
                // JoinType.INNER)
                // .get("book_categories").in(categories);

                // List<Predicate> predicates = new ArrayList<>();
                // Join<Object, Object> categoriesJoin = root.join("categories",
                // JoinType.INNER);

                // categoriesJoin.get("aaaaaaaaaaaaaaaa");
                // return cb.or(predicates.toArray(new Predicate[0]));
                // Join<Object, Object> categoriesJoin = root.join("categories", JoinType.INNER)
                // .on(cb.equal(root.get("id"), root.get("categories").get("book").get("id")));

                // return root.get("categories").in(categories);

                // Join<Object, Object> categoriesJoin = root.join("categories",
                // JoinType.INNER);
                // return cb.equal(categoriesJoin.get("id"), 1);

                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Book> subRoot = subquery.from(Book.class);
                subquery.select(subRoot.get("id")).where(subRoot.joinSet("categories").in(categories));

                return cb.in(root.get("id")).value(subquery);

            }
        };
    }
}
