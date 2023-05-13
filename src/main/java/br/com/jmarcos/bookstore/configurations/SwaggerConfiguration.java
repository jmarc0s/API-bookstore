package br.com.jmarcos.bookstore.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@SecurityScheme(type = SecuritySchemeType.HTTP, bearerFormat = "JWT", name = "Authorization", scheme = "Bearer")
public class SwaggerConfiguration {

        @Bean
        public OpenAPI basOpenAPI() {

                ApiResponse InternalServerError = new ApiResponse().description("Internal Server Error");

                // caso eu tenha alguma resposta em json para minhas respostas http, basta eu
                // adicionar esse pequeno trecho de codigo entre new ApiResponse() e
                // .description (é preciso fazer as alterações necessarias para que a repsoata
                // se adapte a minha resposta)
                /*
                 * .content(
                 * new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                 * new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                 * new Example().value(
                 * "{\"code\" : 500, \"Status\" : \"Internal Server Error\", \"Message\" :\"Something went wrong, please try again in a few minutes\" }"
                 * ))))
                 */

                ApiResponse badRequest = new ApiResponse()
                                .description("Bad Request");

                ApiResponse permissionDenied = new ApiResponse()
                                .description("Permission denied");

                ApiResponse conflict = new ApiResponse()
                                .description("Data Conflict");

                ApiResponse ok = new ApiResponse()
                                .description("Successful request");

                Components component = new Components();

                // essas repostas que eu adiono ao component são repostas que serão usadas como
                // exemplo das minhas repostas dos meus endpoints
                component.addResponses("InternalServerError", InternalServerError);
                component.addResponses("badRequest", badRequest);
                component.addResponses("permissionDenied", permissionDenied);
                component.addResponses("conflict", conflict);
                component.addResponses("ok", ok);
                return new OpenAPI()
                                .components(component)
                                .info(new Info().title("Bookstore API Documentation").version("1.0.0")
                                                .description("a simple API for a bookstore"));
        }

        /*
         * @Bean
         * public Docket api() {
         * return new Docket(DocumentationType.SWAGGER_2)
         * .apiInfo(getInfo())
         * .select()
         * .apis(RequestHandlerSelectors.any())
         * .paths(PathSelectors.any())
         * .build()
         * }
         */

}
