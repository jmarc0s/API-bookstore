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

// isso é um filter que vai rodar antes de qualquer requisição, ele irá
// verificar se o usuario está logado ou não
public class AuthenticateTokenFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private PersonRepository personRepository;

    // esse construtor é necessario, pois não podemos usar o Autowired no
    // tokenService Porque ela não é um bean gerenciado pelo Spring
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
        String token = request.getHeader("Authorization"); // capturo o valor do atributo Authorization do header da
                                                           // requisição
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7, token.length()); // o metodo substring separa a string em 2 strings, ele recebe como
                                                   // parametro, o numero da posição onde ele vai separar e o tamanho
                                                   // total da string. Nesse caso, colocamos o 7, pois "Bearer " contem
                                                   // exatamente 7 caracteres contando com o espaço
    }

    private void authenticatePerson(String token) {
        Long personId = this.tokenService.getPersonId(token);
        Optional<Person> person = personRepository.findById(personId);
        UsernamePasswordAuthenticationToken authPerson = new UsernamePasswordAuthenticationToken(person.get(), null,
                person.get().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authPerson); // dizendo ao spring que o usuario está
                                                                          // autenticado
    }
}
