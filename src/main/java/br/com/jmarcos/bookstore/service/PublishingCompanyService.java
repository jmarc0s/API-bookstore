package br.com.jmarcos.bookstore.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.repository.PublishingCompanyRepository;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PublishingCompanyService {
    private final PublishingCompanyRepository publishingCompanyRepository;
    private final BookService bookService;

    @Autowired
    public PublishingCompanyService(PublishingCompanyRepository publishingCompanyRepository, BookService bookService) {
        this.publishingCompanyRepository = publishingCompanyRepository;
        this.bookService = bookService;
    }

    public Page<PublishingCompany> search(Pageable pageable) {
        return publishingCompanyRepository.findAll(pageable);
    }

    public PublishingCompany save(PublishingCompany publishingCompany) {

        if (this.existsByName(publishingCompany.getName())) {
            throw new ConflictException("PublishingCompany name is already in use");
        }

        return publishingCompanyRepository.save(publishingCompany);
    }

    public boolean existsByName(String name) {
        Optional<PublishingCompany> exists = publishingCompanyRepository.findByName(name);

        return exists.isPresent();
    }

    public PublishingCompany searchById(Long id) {
        return publishingCompanyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PublishingCompany not found with the given id"));
    }

    public PublishingCompany searchByName(String name) {
        return publishingCompanyRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("PublishingCompany not found with the given name"));
    }

    @Transactional
    public void delete(Long id) {
        PublishingCompany publishingCompany = this.searchById(id);

        for (Book book : bookService.searchByPublishingCompany(id)) {
            this.bookService.deleteStorehouseBook(book.getId());
        }

        this.publishingCompanyRepository.delete(publishingCompany);

    }

    public PublishingCompany update(PublishingCompany publishingCompanyUpdate, Long id) {
        PublishingCompany oldPublishingCompany = this.searchById(id);

        if (!Objects.equals(oldPublishingCompany.getName(), publishingCompanyUpdate.getName())
                && this.existsByName(publishingCompanyUpdate.getName())) {

            throw new ConflictException("PublishingCompany name is already in use");
        }

        PublishingCompany updattedPublishimgCompany = fillUpdate(oldPublishingCompany, publishingCompanyUpdate);

        return this.publishingCompanyRepository.save(updattedPublishimgCompany);
    }

    private PublishingCompany fillUpdate(PublishingCompany oldPublishingCompany,
            PublishingCompany publishingCompanyUpdate) {

        publishingCompanyUpdate.getAddress().setId(oldPublishingCompany.getAddress().getId());

        oldPublishingCompany.setName(publishingCompanyUpdate.getName());
        oldPublishingCompany.setUrl(publishingCompanyUpdate.getUrl());
        oldPublishingCompany.setAddress(publishingCompanyUpdate.getAddress());
        oldPublishingCompany.setPhone(publishingCompanyUpdate.getPhone());
        return oldPublishingCompany;
    }

}
