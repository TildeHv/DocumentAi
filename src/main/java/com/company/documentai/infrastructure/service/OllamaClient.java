package com.company.documentai.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaClient {

    private final ObjectMapper objectMapper;
    private final RestClient.Builder restClientBuilder;
    private final String ollamaUrl;
    private final String model;

    public OllamaClient(
            final ObjectMapper objectMapper,
            final RestClient.Builder restClientBuilder,
            @Value("${ollama.url:http://localhost:11434}") final String ollamaUrl,
            @Value("${ollama.model:qwen3}") final String model
    ) {
        this.objectMapper = objectMapper;
        this.restClientBuilder = restClientBuilder;
        this.ollamaUrl = ollamaUrl;
        this.model = model;
    }

    public String summarize(
            @NonNull final String text
    ) {

        final Map<String, Object> request = Map.of(
                "model",
                model,
                "messages",
                new Object[]{
                        Map.of(
                                "role",
                                "system",
                                "content",
                                """
                                You are an assistant that summarizes documents.
                                Be clear and concise.
                                Include the most important points and conclusions.
                                """
                        ),
                        Map.of(
                                "role",
                                "user",
                                "content",
                                text
                        )
                },
                "stream",
                false
        );

        final String response = restClientBuilder
                .baseUrl(ollamaUrl)
                .build()
                .post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        return extractText(response);
    }

    private String extractText(
            @NonNull final String response
    ) {

        try {
            final JsonNode root =
                    objectMapper.readTree(response);

            final String content =
                    root.path("message")
                            .path("content")
                            .asText();

            if (content.isBlank()) {
                throw new IllegalStateException(
                        "No text found in Ollama response"
                );
            }

            return content;

        } catch (IllegalStateException e) {
            throw e;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not parse Ollama response",
                    e
            );
        }
    }
}