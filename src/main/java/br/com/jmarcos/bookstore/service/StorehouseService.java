package br.com.jmarcos.bookstore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Storehouse;
import br.com.jmarcos.bookstore.repository.StorehouseRepository;
import jakarta.transaction.Transactional;

@Service
public class StorehouseService {
    private final StorehouseRepository storehouseRepository;

    @Autowired
    public StorehouseService(StorehouseRepository storehouseRepository) {
        this.storehouseRepository = storehouseRepository;
    }

    public Storehouse save(Storehouse storehouse) {
        return storehouseRepository.save(storehouse);
    }

    public boolean existsByCode(Integer code) {
        Optional<Storehouse> exist = storehouseRepository.findByCode(code);
        return exist.isPresent();
    }

    public Page<Storehouse> search(Pageable pageable) {
        return this.storehouseRepository.findAll(pageable);
    }

    public Optional<Storehouse> searchByID(Long id) {
        return this.storehouseRepository.findById(id);
    }

    @Transactional
    public Boolean delete(Long id) {
        Optional<Storehouse> storehouse = this.searchByID(id);

        if (storehouse.isPresent()) {
            this.storehouseRepository.delete(storehouse.get());
            return true;
        }

        return false;
    }

    public Optional<Storehouse> update(Storehouse newstorehouse) {
        Optional<Storehouse> oldStorehouse = storehouseRepository.findById(newstorehouse.getId());

        return oldStorehouse.isPresent()
                ? Optional.of(this.save(this.fillUpdateStorehouse(oldStorehouse.get(), newstorehouse)))
                : Optional.empty();
    }

    public Storehouse fillUpdateStorehouse(Storehouse oldStorehouse, Storehouse newStorehouse) {
        oldStorehouse.setPhone(newStorehouse.getPhone());

        return oldStorehouse;
    }

    public Optional<Storehouse> searchByCode(Integer code) {
        return this.storehouseRepository.findByCode(code);
    }

    public List<Storehouse> findStorehousesByAddress(String street, int number, String city, String state,
            String zipCode) {
        return storehouseRepository.findByAddressStreetAndAddressNumberAndAddressCityAndAddressStateAndAddressZipCode(
                street, number, city, state, zipCode);
    }

}
