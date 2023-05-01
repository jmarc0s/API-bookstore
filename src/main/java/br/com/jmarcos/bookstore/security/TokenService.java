package br.com.jmarcos.bookstore.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
    @Value("${bookstore.jwt.expiration}") /* "1 dia" */
    private String expiration;

    @Value("${bookstore.jwt.secret}")
    private String secret;

    public String createToken(Authentication authentication) {
        Person person = (Person) authentication.getPrincipal();
        Date today = new Date();
        Date deadline = new Date(today.getTime() + Long.parseLong(expiration));
        return Jwts.builder()
                .setIssuer("bookstore API")
                .setSubject(person.getId().toString()) // setando o usuario (dono do token). obs: é necessario converter
                                                       // para string
                .setIssuedAt(today) // setando a data de criação do token
                .setExpiration(deadline) // setando a data de expiração do token
                // configurando criptografia do token
                .signWith(SignatureAlgorithm.HS256, secret) // o primeiro parametro informa qual o algoritimo de
                                                            // criptografia e o segundo informa qual é a senha
                .compact(); // compacta tudo
    }

    public boolean isAValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token); // parseClaimsJws recupera o token e as
                                                                            // informações dele, setSigningKey é a senha
                                                                            // e parser descriptografa
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getPersonId(String token) {
        Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject()); // retorna o id do usuario do token
    }
}
