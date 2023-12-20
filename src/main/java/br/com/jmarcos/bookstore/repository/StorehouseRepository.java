package br.com.jmarcos.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.bookstore.model.Storehouse;

@Repository
public interface StorehouseRepository extends JpaRepository<Storehouse, Long> {

      Optional<Storehouse> findByCode(Integer code);

      Optional<Storehouse> findById(Integer id);

}
