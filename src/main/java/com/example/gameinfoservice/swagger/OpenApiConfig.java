package com.example.gameinfoservice.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Game Info",
                description = "Game Info Sevice", version = "1.0.0",
                contact = @Contact(
                        name = "Danik",
                        email = ""
                )
        )
)
public class OpenApiConfig {

}
