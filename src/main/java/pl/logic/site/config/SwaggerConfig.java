package pl.logic.site.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${sumproject.openapi.dev-url}")
    private String devUrl;

    @Value("${sumproject.openapi.prod-url}")
    private String prodUrl;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("242349@edu.p.lodz.pl");
        contact.setName("242349");
        contact.setUrl("https://github.com/SlawomirA/ZZJPBackend");

        License beerwareLicense = new License().name("Beerware License").url("https://spdx.org/licenses/Beerware.html");

        Info info = new Info()
                .title("Logic Module API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage SumProject logic.")
                .license(beerwareLicense);

        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                        .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                        .info(info)
                        .servers(List.of(devServer, prodServer));
    }
}