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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import br.com.jmarcos.bookstore.controller.dto.person.PersonLoginDTO;
import br.com.jmarcos.bookstore.controller.dto.token.TokenDTO;
import br.com.jmarcos.bookstore.security.TokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

      // FIXME
      // retirar essas anotações. (utlizar o construtor para a injeção das
      // dependencias)
      @Autowired
      private AuthenticationManager authenticationManager;
      @Autowired
      private TokenService tokenService;

      @Operation(summary = "Login in the system", description = "login to access more features", responses = {
                  @ApiResponse(responseCode = "200", ref = "ok"),
                  @ApiResponse(responseCode = "400", ref = "badRequest"),
                  @ApiResponse(responseCode = "401", description = "invalid user")
      })
      @PostMapping
      public ResponseEntity<Object> Login(
                  @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", examples = {
                              @ExampleObject(value = "{"
                                          + "\"login\": \"user@gmail.com\","
                                          + "\"password\": \"user123\""
                                          + "}"),
                  })) @RequestBody @Valid PersonLoginDTO personLoginDTO) {

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = personLoginDTO.convert();

            // FIXME
            // RETIRAR ESSE TRY CATCH E ADICIONAR UM METODO QUE CAPTURA ESSA EXPTION NO
            // HANDLEREXCEPTION
            try {
                  Authentication authentication = authenticationManager
                              .authenticate(usernamePasswordAuthenticationToken);
                  String token = tokenService.createToken(authentication);

                  return ResponseEntity.ok(new TokenDTO(token, "Bearer"));
            } catch (AuthenticationException e) {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid parameters");
            }

      }
}
