package br.com.jmarcos.bookstore.service.exceptions;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String detail) {
        super(detail);
    }
}
