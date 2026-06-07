package africa.royalsettle.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI royalSettleOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Royal Settle API")
                        .description("API documentation for onboarding, authentication, OTP, and thrift operations.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Royal Settle")
                                .url("https://royalsettle.africa")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter the access token returned by the login endpoint.")));
    }
}
