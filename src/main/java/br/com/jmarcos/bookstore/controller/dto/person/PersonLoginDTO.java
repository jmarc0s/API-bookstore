package br.com.jmarcos.bookstore.controller.dto.person;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonLoginDTO {
    @NotEmpty
    @NotNull
    private String login;
    @NotEmpty
    @NotNull
    private String password;

    public UsernamePasswordAuthenticationToken convert() {

        return new UsernamePasswordAuthenticationToken(login, password);
    }
}
