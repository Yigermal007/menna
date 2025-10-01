package com.example.mastercardsend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Mastercard Send Integration API",
        version = "1.0.0",
        description = "Gateway API for initiating and tracking Mastercard Send P2P transfers.",
        contact = @Contact(name = "API Support")
    ),
    servers = {@Server(url = "/", description = "Default server")}
)
public class OpenApiConfig { }

