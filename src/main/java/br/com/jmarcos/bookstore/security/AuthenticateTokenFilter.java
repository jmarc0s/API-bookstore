package br.com.jmarcos.bookstore.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.repository.PersonRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticateTokenFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private PersonRepository personRepository;

    public AuthenticateTokenFilter(TokenService tokenService, PersonRepository personRepository) {
        this.tokenService = tokenService;
        this.personRepository = personRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.getToken(request);
        if (this.tokenService.isAValidToken(token)) {
            this.authenticatePerson(token);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization"); 
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7, token.length());
    }

    private void authenticatePerson(String token) {
        Long personId = this.tokenService.getPersonId(token);
        Optional<Person> person = personRepository.findById(personId);
        UsernamePasswordAuthenticationToken authPerson = new UsernamePasswordAuthenticationToken(person.get(), null,
                person.get().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authPerson);
    }
}
