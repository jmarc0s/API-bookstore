package br.com.jmarcos.bookstore.specifications;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import br.com.jmarcos.bookstore.model.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BookSpecification {

    public static Specification<Book> bookHasPrice(BigDecimal price) {

        if (price == null) {
            return null;
        }
        // return (root, criteriaQuery, criteriaBuilder) ->
        // criteriaBuilder.like(root.get("price"), price );

        return new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("price"), price);
            }
        };

    }

    public static Specification<Book> bookHasYear(Integer year) {

        // return (root, criteriaQuery, criteriaBuilder) ->
        // criteriaBuilder.like(root.get("year"), year );

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
}
