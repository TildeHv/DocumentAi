package com.company.documentai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TestcontainersSmokeTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    @DisplayName("Testcontainers ska kunna starta en PostgreSQL-container")
    void testPostgresContainerStarts() {
        assertThat(postgres.isRunning()).isTrue();
        System.out.println("PostgreSQL container is running: " + postgres.getJdbcUrl());
    }
}
