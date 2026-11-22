package com.bilanprojets.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Bilan Annuel & Gestion des Fonctionnalités de Projets")
                        .version("1.0.0")
                        .description("Application REST Spring Boot 3.1.3 permettant de suivre la situation annuelle des projets " +
                                "(année, intitulé, description, fonctionnalités avec statut, fonctionnalités principales, " +
                                "actions durant l'année en cours, situation de projet) avec persistance MySQL.")
                        .contact(new Contact()
                                .name("Équipe Direction de Projets")
                                .email("projets@entreprise.com"))
                        .license(new License().name("Apache 2.0")));
    }
}
