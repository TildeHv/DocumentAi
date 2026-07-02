# DocumentAI

A Java-based service for managing and processing documents using Spring Boot, PostgreSQL, and Spring AI.

## API Documentation

The project uses [SpringDoc OpenAPI](https://springdoc.org/) to generate Swagger UI and API documentation. When the application is running, you can access it at:

- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **OpenAPI Spec**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker & Docker Compose (for the database)

### Setup and Running

1. **Start the Database**
   The application requires a PostgreSQL database. You can start it using Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. **Run the Application**
   You can run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

The server will start on port `8081` by default.

## Project Structure

The project follows a clean architecture-inspired structure:

- `app/`: Application configuration and entry point.
- `domain/`: Business models and service logic.
- `web/`: REST controllers and web-specific configuration.
- `infrastructure/`: Database entities, repositories, and external client configurations (AI, etc.).
- `resources/db/changelog/`: Liquibase database migration scripts.

## Features

- **File Upload**: Upload documents and get metadata (filename, type).
- **Database Storage**: Documents are persisted in a PostgreSQL database with UUIDs.
- **API Documentation**: Interactive Swagger UI for testing endpoints.
- **Docker Support**: Pre-configured Docker Compose for easy local development.
