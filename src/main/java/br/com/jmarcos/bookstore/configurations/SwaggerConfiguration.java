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

        // FIXME
        // fazer com que esse metodo torne a utilização do header Authorization padrão
        // para todos os endpoints
        @Bean
        public OpenAPI basOpenAPI() {

                ApiResponse internalServerError = new ApiResponse().description("Internal Server Error");

                ApiResponse badRequest = new ApiResponse()
                                .description("Bad Request");

                ApiResponse permissionDenied = new ApiResponse()
                                .description("Permission Denied");

                ApiResponse conflict = new ApiResponse()
                                .description("Data Conflict");

                ApiResponse ok = new ApiResponse()
                                .description("Successful Request");

                ApiResponse ResourceNotFound = new ApiResponse().description("Resource Not Found");
                Components component = new Components();

                component.addResponses("InternalServerError", internalServerError);
                component.addResponses("badRequest", badRequest);
                component.addResponses("permissionDenied", permissionDenied);
                component.addResponses("conflict", conflict);
                component.addResponses("ok", ok);
                component.addResponses("ResourceNotFound", ResourceNotFound);
                return new OpenAPI()
                                .components(component)
                                .info(new Info().title("Bookstore API Documentation").version("2.0.1")
                                                .description("a simple API for a bookstore"));
        }

}
