package spengergasse.at.sj2425scherzerrabar;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("sj24-25-scherzer-rabar");

        Contact myContact1 = new Contact();
        myContact1.setName("Aron Rabar");
        myContact1.setEmail("sch210635@spengergasse.at");

        Info information = new Info()
                .title("'Basic' sj24-25-scherzer-rabar API")
                .version("1.0")
                .description("This APIs endpoints to manage your sj24-25-scherzer-rabar implementation.")
                .contact(myContact1);

        // 🛡️ Bearer Auth definieren
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // 🛡️ Basic Auth definieren
        SecurityScheme basicScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic");

        // Security Requirements
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        Tag authTag = new Tag()
                .name("auth-rest-controller")
                .description("⚠️⚠️⚠️ USE THIS TO GET YOUR TOKEN FOR THE REST OF THE REQUESTS. ⚠️⚠️⚠️ Authentication Controller.");


        return new OpenAPI()
                .info(information)
                .servers(List.of(server))
                .addTagsItem(authTag)
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", bearerScheme)
                        .addSecuritySchemes("basicAuth", basicScheme));
    }

}

