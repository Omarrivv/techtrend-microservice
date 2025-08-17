package com.techtrend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentar la API de TechTrend
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configuración principal de OpenAPI
     * @return Configuración de la API
     */
    @Bean
    public OpenAPI techTrendOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de desarrollo");

        Contact contact = new Contact();
        contact.setName("TechTrend Team");
        contact.setEmail("support@techtrend.com");
        contact.setUrl("https://www.techtrend.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("TechTrend E-commerce API")
                .version("1.0.0")
                .contact(contact)
                .description("API completa para la plataforma de e-commerce TechTrend con microservicios de autenticación, catálogo, carrito y pagos.")
                .termsOfService("https://www.techtrend.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
