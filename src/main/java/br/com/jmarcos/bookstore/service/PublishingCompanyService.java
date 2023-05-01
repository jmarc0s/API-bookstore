package br.com.jmarcos.bookstore.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Book;
import br.com.jmarcos.bookstore.model.PublishingCompany;
import br.com.jmarcos.bookstore.repository.PublishingCompanyRepository;
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
        return publishingCompanyRepository.save(publishingCompany);
    }

    public boolean existsByName(String name) {
        Optional<PublishingCompany> exists = publishingCompanyRepository.findByName(name);

        return exists.isPresent();
    }

    public Optional<PublishingCompany> searchById(Long id) {
        return publishingCompanyRepository.findById(id);
    }

    public Optional<PublishingCompany> searchByName(String name) {
        return publishingCompanyRepository.findByName(name);
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<PublishingCompany> publishingCompany = this.searchById(id);

        if (publishingCompany.isPresent()) {

            for (Book book : bookService.searchByPublishingCompany(id)) {
                this.bookService.deleteStorehouseBook(book.getId());
            }
            this.publishingCompanyRepository.delete(publishingCompany.get());
            return true;
        }

        return false;
    }

    public Optional<PublishingCompany> update(PublishingCompany publishingCompanyUpdate, Long id) {
        Optional<PublishingCompany> oldPublishingCompany = publishingCompanyRepository.findById(id);
        PublishingCompany updattedPublishimgCompany = fillUpdate(oldPublishingCompany.get(), publishingCompanyUpdate);
        this.save(updattedPublishimgCompany);
        return Optional.of(updattedPublishimgCompany);
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
