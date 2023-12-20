package br.com.jmarcos.bookstore.service.exceptions;

public class ResourceNotFoundException extends RuntimeException {
      public ResourceNotFoundException(String detail) {
            super(detail);
      }
}
