package com.company.documentai.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OllamaClientTest {

    private OllamaClient ollamaClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {

        final ObjectMapper objectMapper =
                new ObjectMapper();

        final RestClient.Builder builder =
                RestClient.builder();

        mockServer =
                MockRestServiceServer
                        .bindTo(builder)
                        .build();

        ollamaClient =
                new OllamaClient(
                        objectMapper,
                        builder,
                        "http://localhost:11434",
                        "qwen3"
                );
    }

    @Test
    void shouldSummarizeText() {

        final String text = """
                This is a test document.
                The document is about AI and RAG.
                """;

        final String response = """
                {
                  "model": "qwen3",
                  "message": {
                    "role": "assistant",
                    "content": "The document is about AI and RAG."
                  },
                  "done": true
                }
                """;

        mockServer
                .expect(
                        requestTo(
                                "http://localhost:11434/api/chat"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andExpect(
                        header(
                                "Content-Type",
                                "application/json"
                        )
                )
                .andExpect(
                        content().json("""
                                {
                                  "model": "qwen3",
                                  "messages": [
                                    {
                                      "role": "system",
                                      "content": "You are an assistant that summarizes documents.\\nBe clear and concise.\\nInclude the most important points and conclusions.\\n"
                                    },
                                    {
                                      "role": "user",
                                      "content": "This is a test document.\\nThe document is about AI and RAG.\\n"
                                    }
                                  ],
                                  "stream": false
                                }
                                """)
                )
                .andRespond(
                        withSuccess(
                                response,
                                MediaType.APPLICATION_JSON
                        )
                );

        final String result =
                ollamaClient.summarize(text);

        assertThat(result)
                .isEqualTo(
                        "The document is about AI and RAG."
                );

        mockServer.verify();
    }

    @Test
    void shouldThrowExceptionWhenResponseDoesNotContainText() {

        final String text =
                "Test document";

        final String response = """
                {
                  "model": "qwen3",
                  "message": {
                    "role": "assistant"
                  },
                  "done": true
                }
                """;

        mockServer
                .expect(
                        requestTo(
                                "http://localhost:11434/api/chat"
                        )
                )
                .andExpect(
                        method(HttpMethod.POST)
                )
                .andRespond(
                        withSuccess(
                                response,
                                MediaType.APPLICATION_JSON
                        )
                );

        assertThatThrownBy(
                () -> ollamaClient.summarize(text)
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "No text found in Ollama response"
                );

        mockServer.verify();
    }
}