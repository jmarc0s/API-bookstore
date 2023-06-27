package br.com.jmarcos.bookstore.controller.dto.person;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.validation.constraints.NotBlank;
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
    
    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String login;

    @NotBlank (message = "must not be blank")
    @NotNull (message = "must not be null")
    private String password;

    public UsernamePasswordAuthenticationToken convert() {

        return new UsernamePasswordAuthenticationToken(login, password);
    }
}
