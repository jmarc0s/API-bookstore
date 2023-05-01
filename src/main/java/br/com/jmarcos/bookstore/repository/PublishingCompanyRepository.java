package br.com.jmarcos.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.PublishingCompany;

@Repository
public interface PublishingCompanyRepository extends JpaRepository<PublishingCompany, Long> {

    Optional<PublishingCompany> findByName(String name);

}
