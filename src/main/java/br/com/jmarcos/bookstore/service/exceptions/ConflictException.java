package br.com.jmarcos.bookstore.service.exceptions;

public class ConflictException extends RuntimeException{
    public ConflictException(String detail) {
        super(detail);
    }
}
