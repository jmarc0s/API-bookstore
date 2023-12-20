package br.com.jmarcos.bookstore.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.jmarcos.bookstore.model.Person;
import br.com.jmarcos.bookstore.repository.PersonRepository;

@Service
public class AuthService implements UserDetailsService {

      // FIXME
      // retirar anotação
      @Autowired
      private PersonRepository personRepository;

      @Override
      public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            Optional<Person> person = personRepository.findByEmail(email);
            if (person.isPresent()) {
                  return person.get();
            }
            throw new UsernameNotFoundException("Dados invalidos!!");
      }

}
