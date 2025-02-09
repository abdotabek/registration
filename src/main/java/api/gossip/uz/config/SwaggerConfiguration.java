package api.gossip.uz.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPi() {
        Info info = new Info()
                .title("Gossip.uz API-lar")
                .version("1.0.0")
                .description("Quyida G'iybat.uz loyihasi uchun API hujjatlar tagdim qilingan.")
                .contact(new Contact()
                        .name("ADMIN")
                        .email("admin@gmail.com")
                        .url("https://t.me/abdulazizovOtabek")
                )
                .license(new License()
                        .name("Videohub.uz")
                        .url("https://videohub.uz/")
                )
                .termsOfService("Savol javob guruhi: https://t.me/code_uz_group");

        Server server1 = new Server()
                .description("Local")
                .url("http://localhost:8080");

        Server server2 = new Server()
                .description("DEV")
                .url("https://dasturlash.uz");

        Server server3 = new Server()
                .description("PROD")
                .url("https://uzkassa.uz");

        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("bearerAuth");

        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.setName("bearerAuth");
        securityScheme.setType(SecurityScheme.Type.HTTP);
        securityScheme.bearerFormat("JWT");
        securityScheme.setIn(SecurityScheme.In.HEADER);
        securityScheme.setScheme("bearer");

        Components components = new Components();
        components.addSecuritySchemes("bearerAuth", securityScheme);

        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);
        openAPI.setServers(List.of(server1, server2, server3));
        openAPI.setSecurity(List.of(securityRequirement));
        openAPI.components(components);

        return openAPI;
    }
}
