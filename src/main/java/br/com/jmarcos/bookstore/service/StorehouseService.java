package br.com.jmarcos.bookstore.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.repository.StorehouseRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class StorehouseService {
      private final StorehouseRepository storehouseRepository;

      // FIXME
      // retirar anotação
      @Autowired
      public StorehouseService(StorehouseRepository storehouseRepository) {
            this.storehouseRepository = storehouseRepository;
      }

      public Storehouse save(Storehouse storehouse) {

            // FIXME
            // substituir essa validação pelo metodo do repository: existsByCode
            if (this.existsByCode(storehouse.getCode())) {
                  throw new ConflictException("Storehouse code is already in use.");
            }

            return storehouseRepository.save(storehouse);
      }

      public boolean existsByCode(Integer code) {
            Optional<Storehouse> exist = storehouseRepository.findByCode(code);
            return exist.isPresent();
      }

      public Page<Storehouse> search(Pageable pageable) {
            return this.storehouseRepository.findAll(pageable);
      }

      public Storehouse searchById(Long id) {
            return this.storehouseRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Storehouse not found with the given id"));
      }

      @Transactional
      public void delete(Long id) {
            Storehouse storehouse = this.searchById(id);

            this.storehouseRepository.delete(storehouse);

      }

      public Storehouse update(Storehouse newstorehouse) {
            Storehouse oldStorehouse = this.searchById(newstorehouse.getId());

            return this.storehouseRepository.save(this.fillUpdateStorehouse(oldStorehouse, newstorehouse));
      }

      public Storehouse fillUpdateStorehouse(Storehouse oldStorehouse, Storehouse newStorehouse) {
            oldStorehouse.setPhone(newStorehouse.getPhone());

            return oldStorehouse;
      }

      public Storehouse searchByCode(Integer code) {
            return this.storehouseRepository.findByCode(code)
                        .orElseThrow(() -> new ResourceNotFoundException("Storehouse not found with the given code"));
      }

}
