package br.com.jmarcos.bookstore.service.exceptions;

public class InvalidConfirmationCodeException extends RuntimeException {
      public InvalidConfirmationCodeException(String detail) {
            super(detail);
      }
}
