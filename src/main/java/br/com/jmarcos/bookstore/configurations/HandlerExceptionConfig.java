package br.com.jmarcos.bookstore.configurations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.jmarcos.bookstore.service.exceptions.BadRequestException;
import br.com.jmarcos.bookstore.service.exceptions.ConflictException;
import br.com.jmarcos.bookstore.service.exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class HandlerExceptionConfig {
    

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDetails> handlerBadRequestException(BadRequestException exception){
        ExceptionDetails details = new ExceptionDetails("Bad Request Exception. Please, Submit a Valid Request", exception.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDetails> handlerResourceNotFoundException(ResourceNotFoundException exception){
        ExceptionDetails details = new ExceptionDetails("Resource Dot Found in Database", exception.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionDetails> handlerConflictException(ConflictException exception) {
        ExceptionDetails details = new ExceptionDetails("Data Conflict", exception.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }


}
