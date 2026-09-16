# DocumentAI

DocumentAI is a Java Spring Boot application for uploading, storing, processing, and summarizing documents using PostgreSQL and Ollama.

## Features

* Upload documents
* Store documents in PostgreSQL
* Download and delete documents
* Extract text from TXT, PDF, DOC, and DOCX files
* Generate AI summaries
* Database migrations with Liquibase
* REST API with OpenAPI/Swagger

## Requirements

* Java 21
* Docker & Docker Compose

## Getting Started

Start the required infrastructure:

```bash
docker compose up -d
```

Start the application from your IDE using `DocumentaiApplication`.

Alternatively, using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

OpenAPI:

http://localhost:8080/v3/api-docs

## Architecture

The application follows an Onion Architecture approach. The code is separated into domain, application, infrastructure, and web layers to keep business logic independent from technical details such as the database, file parsing, and Ollama.

The main goal is to keep dependencies pointing inwards. This makes the application easier to understand, test, and change without coupling the core logic to specific infrastructure technologies.

See [Architecture](docs/architecture.md) for a detailed explanation of the structure and the reasoning behind the implementation.

## Documentation

* [Architecture](docs/architecture.md) — structure and design decisions
* [API](docs/api.md) — endpoints and API usage
* [Ollama](docs/ollama.md) — AI summarization and configuration
* [Development](docs/development.md) — local development, Docker, database, and Liquibase
