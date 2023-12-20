package br.com.jmarcos.bookstore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.jmarcos.bookstore.repository.PersonRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations {

      // FIXME
      // retirar anotação Autowired
      @Autowired
      private TokenService tokenService;

      @Autowired
      private PersonRepository personRepository;

      @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                        .authorizeHttpRequests((requests) -> {
                              requests
                                          .requestMatchers(HttpMethod.GET, "/books").permitAll()
                                          .requestMatchers(HttpMethod.GET, "/books/**").permitAll()
                                          .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.POST, "/authors").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.DELETE, "/authors/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.PUT, "/authors/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.GET, "/authors").permitAll()
                                          .requestMatchers(HttpMethod.GET, "/authors/**").permitAll()
                                          .requestMatchers("/storehouses").hasRole("ADMIN")
                                          .requestMatchers("/storehouses/**").hasRole("ADMIN")
                                          .requestMatchers("/publishingCompanies").hasRole("ADMIN")
                                          .requestMatchers("/publishingCompanies/**").hasRole("ADMIN")
                                          .requestMatchers("/permissions").hasRole("ADMIN")
                                          .requestMatchers("/permissions/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.POST, "/persons").permitAll()
                                          .requestMatchers(HttpMethod.POST,
                                                      "/persons/change_email_and_resend_confirmation_code")
                                          .permitAll()
                                          .requestMatchers(HttpMethod.POST, "/persons/confirm_code").permitAll()
                                          .requestMatchers(HttpMethod.GET, "/persons").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.GET, "/persons/profile").authenticated()
                                          .requestMatchers(HttpMethod.PUT, "/persons/profile").authenticated()
                                          .requestMatchers(HttpMethod.DELETE, "/persons/profile").authenticated()
                                          .requestMatchers(HttpMethod.GET, "/persons/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.DELETE, "/persons/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.PATCH, "/persons/**").hasRole("ADMIN")
                                          .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                                          .requestMatchers(HttpMethod.GET,
                                                      "/v3/api-docs/**",
                                                      "/swagger-ui/**",
                                                      "/swagger-ui.html")
                                          .permitAll()
                                          .anyRequest().authenticated();
                        }).csrf().disable()
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and().addFilterBefore(new AuthenticateTokenFilter(tokenService, personRepository),
                                    UsernamePasswordAuthenticationFilter.class);

            return http.build();
      }

      @Bean
      public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();

      }

      @Bean
      public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
      }

}
