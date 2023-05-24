package br.com.jmarcos.bookstore.configurations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDetails {
    private String title;

    private String details;

    private int status;
}
