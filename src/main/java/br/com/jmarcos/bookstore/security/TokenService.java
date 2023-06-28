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
    @Value("${bookstore.jwt.expiration}")
    private String expiration;

    @Value("${bookstore.jwt.secret}")
    private String secret;

    public String createToken(Authentication authentication) {
        Person person = (Person) authentication.getPrincipal();
        Date today = new Date();
        Date deadline = new Date(today.getTime() + Long.parseLong(expiration));
        return Jwts.builder()
                .setIssuer("bookstore API")
                .setSubject(person.getId().toString())
                .setIssuedAt(today)
                .setExpiration(deadline)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean isAValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token); 
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getPersonId(String token) {
        Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }
}
