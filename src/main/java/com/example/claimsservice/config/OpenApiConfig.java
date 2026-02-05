package com.example.claimsservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Claims Processing Service API",
        version = "1.0.0",
        description = """
            REST API for managing insurance claims in TPA system.

            Features:
            • Claim submission
            • Claim status lifecycle
            • Policy validation
            • Audit tracking
            """,
        contact = @Contact(
            name = "Claims Platform Team",
            email = "claims-support@example.com"
        ),
        license = @License(
            name = "Internal Use Only"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Local Environment"
        ),
        @Server(
            url = "https://api.example.com",
            description = "Production Environment"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    /**
     * This class only holds OpenAPI annotations.
     * Springdoc scans it automatically.
     */

}
