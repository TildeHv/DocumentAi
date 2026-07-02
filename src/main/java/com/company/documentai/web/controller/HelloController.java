package com.company.documentai.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //Visar att klassen är ett api. Returnerar JSON eller text. Kan ta emot HTTP-anrop
@RequestMapping("/api") //Sätter en "bas-URL"
@Tag(name = "Hello API", description = "En enkel test endpoint") //För Swagger(OpenAPI). Grupperar endpoints i Swagger UI. Ger beskrivning i dokumentationen.
public class HelloController {
    @GetMapping("/hello") //HTTP Get request på URL /api/hello
    @Operation(summary = "Returnerar hej") //Swagger dokumentation. Beskriver vad endpoint gör. Visas i Swagger UI

    //Körs när någon anropar GET /api/hello
    public Response hello() {
        return new Response();
    }

    @Getter
    public static class Response {
        private final String message = "hej";
    }
}
