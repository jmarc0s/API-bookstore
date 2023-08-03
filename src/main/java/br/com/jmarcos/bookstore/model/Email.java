package br.com.jmarcos.bookstore.model;

import java.time.LocalDateTime;

import br.com.jmarcos.bookstore.model.enums.StatusEmailEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity
// @Table(name = "email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    private Long id;
    private String emailFrom;
    private String emailTo;
    private Person recipient;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String text;
    private LocalDateTime sendDateEmail;
    private StatusEmailEnum statusEmail;
}
