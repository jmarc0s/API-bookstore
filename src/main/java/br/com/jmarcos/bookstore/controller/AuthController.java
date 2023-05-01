package br.com.jmarcos.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jmarcos.bookstore.controller.dto.person.PersonLoginDTO;
import br.com.jmarcos.bookstore.controller.dto.token.TokenDTO;
import br.com.jmarcos.bookstore.security.TokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // para que esse atributo seja injetado corretamente, é necessario ter o metodo
    // AuthenticationManager anotadocom @Bean na classe de configuração do spring
    // security
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<Object> Login(@RequestBody @Valid PersonLoginDTO personLoginDTO) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = personLoginDTO.convert();

        try {
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            String token = tokenService.createToken(authentication);

            return ResponseEntity.ok(new TokenDTO(token, "Bearer"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid parameters");
        }

    }
}
