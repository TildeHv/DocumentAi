# DocumentAI

A Java-based service for managing and processing documents using Spring Boot, PostgreSQL, Liquibase, Docker, and Ollama.

The application allows users to upload, store, retrieve, delete, and summarize documents. Supported documents are processed to extract their text, which can then be sent to a locally running Ollama model for summarization.

## API Documentation

The project uses [SpringDoc OpenAPI](https://springdoc.org/) to generate Swagger UI and API documentation. When the application is running, you can access it at:

* **Swagger UI**: http://localhost:8080/swagger-ui/index.html
* **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## Getting Started

### Prerequisites

* Java 21
* Maven
* Docker & Docker Compose
* Ollama

### Setup and Running

1. **Start the Database**

   The application requires a PostgreSQL database. It can be started using Docker Compose:

   ```bash
   docker compose up -d
   ```

2. **Start the Application**

   The application can be started by running the `DocumentaiApplication` class from your IDE.

   Alternatively, the application can be started using the Maven wrapper:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows:

   ```bash
   mvnw.cmd spring-boot:run
   ```

   The server will start on port `8080` by default.

3. **Ollama**

   Ollama is used for document summarization. The required model is downloaded automatically when needed, so no manual model download is required.

## Features

* **File Upload**: Upload documents through the REST API.
* **Database Storage**: Documents are stored in PostgreSQL.
* **File Download**: Download the original uploaded document.
* **Document Listing**: Retrieve metadata for stored documents.
* **File Deletion**: Delete documents from the database.
* **Text Extraction**: Extract text from `.txt`, `.pdf`, `.doc`, and `.docx` files.
* **AI Summarization**: Send extracted document text to Ollama for summarization.
* **Database Migrations**: Liquibase is used to manage database schema changes.
* **API Documentation**: Swagger UI and OpenAPI documentation are available.
* **Docker Support**: Docker Compose is used for local PostgreSQL development.

## Project Structure

The project follows an Onion Architecture approach. The domain and application layers are kept independent from infrastructure-specific technologies.

com.company.documentai
├── domain
│   ├── model
│   │   ├── Document.java
│   │   └── DocumentToSave.java
│   └── repository
│       └── DocumentRepository.java
│
├── application
│   ├── port
│   │   ├── DocumentSummarizer.java
│   │   └── DocumentTextExtractor.java
│   └── service
│       └── DocumentService.java
│
├── infrastructure
│   ├── database
│   │   ├── DocumentEntity.java
│   │   ├── DocumentRepositoryImpl.java
│   │   └── SpringDocumentRepository.java
│   ├── document
│   │   └── DocumentTextExtractorImpl.java
│   └── ollama
│       └── OllamaClient.java
│
└── web
    ├── controller
    │   └── DocumentController.java
    ├── mapper
    │   └── DocumentMapper.java
    └── model
        └── DocumentDto.java

### Domain

The `domain` layer contains the core models and repository interfaces used by the application.

The domain does not depend on Spring, PostgreSQL, Ollama, or other infrastructure technologies.

### Application

The `application` layer contains the application services and ports used to communicate with external functionality.

`DocumentService` uses interfaces such as `DocumentRepository`, `DocumentTextExtractor`, and `DocumentSummarizer` instead of depending directly on database or AI implementations.

This keeps the application logic independent from the technologies used to implement these operations.

### Infrastructure

The `infrastructure` layer contains implementations that communicate with external systems.

This includes:

* PostgreSQL persistence using Spring Data JPA.
* Document text extraction using Apache PDFBox and Apache POI.
* Communication with Ollama using Spring `RestClient`.

The infrastructure layer implements interfaces defined by the inner layers.

### Web

The `web` layer contains the REST API.

The `DocumentController` handles HTTP requests and uses the application service to perform document operations.

Web-specific models such as `DocumentDto` and request mappings are kept outside the application and domain layers.

## Supported Document Types

The application currently supports the following file types:

| File Type | Text Extraction |
| --------- | --------------- |
| `.txt`    | Yes             |
| `.pdf`    | Yes             |
| `.doc`    | Yes             |
| `.docx`   | Yes             |

## Document Processing

When a document is uploaded, the application stores the original file in PostgreSQL.

For summarization, the following process is used:

1. The client uploads a document.
2. The REST controller passes the request to the application service.
3. The document is retrieved from the database.
4. The appropriate text extractor extracts text from the document.
5. The extracted text is sent to Ollama.
6. Ollama generates a summary.
7. The summary is returned to the client.

Text extraction is handled by Apache PDFBox for PDF documents and Apache POI for Microsoft Office documents.

## Ollama Configuration

The application communicates with Ollama using the following configuration:

```yaml
ollama:
  url: http://localhost:11434
  model: llama3.2
```

The model is configured in the application properties and is downloaded automatically when required.

If the Spring Boot application and Ollama are running in separate Docker containers, `localhost` should not be used to access Ollama. Instead, the Docker Compose service name should be used, for example:

```yaml
ollama:
  url: http://ollama:11434
```

## API Endpoints

### Upload Document

```http
POST /api/v1/documents
```

Uploads a document to the application.

Supported file extensions:

* `.txt`
* `.pdf`
* `.doc`
* `.docx`

### Get All Documents

```http
GET /api/v1/documents
```

Returns metadata for all stored documents.

### Download Document

```http
GET /api/v1/documents/{id}/download
```

Returns the original file for the specified document.

### Delete Document

```http
DELETE /api/v1/documents/{id}
```

Deletes the specified document.

### Summarize Document

```http
GET /api/v1/documents/{id}/summary
```

Extracts the text from the document and sends it to Ollama to generate a summary.

## Database

The application uses PostgreSQL for document storage.

The database schema is managed using Liquibase. The main changelog is located under:

```text
src/main/resources/db/changelog/
```

The document table contains:

* `id`
* `file_name`
* `file_type`
* `content`
* `created_at`

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means Hibernate validates the database schema instead of creating or modifying it. Database changes are handled through Liquibase migrations.

## Docker

Docker Compose is used to run the PostgreSQL database locally.

The application can either run directly from the IDE or using the Maven wrapper.

When running the application locally and PostgreSQL in Docker, the application can connect to PostgreSQL through:

```text
localhost:5432
```

When both the application and PostgreSQL are running inside Docker Compose, the PostgreSQL service name should be used as the hostname instead.

The same principle applies to Ollama. When Ollama runs in another Docker container, the application should connect to the Ollama container using its Docker Compose service name.

## Why the Application Is Structured This Way

The project follows an Onion Architecture approach to separate the application's core logic from external technologies.

The domain and application layers should not depend directly on infrastructure such as PostgreSQL, Spring Data JPA, PDFBox, or Ollama.

For example, the application service depends on a `DocumentRepository` interface rather than directly on a Spring Data repository. The infrastructure layer provides the implementation of that interface.

The same approach is used for text extraction and document summarization. The application depends on interfaces such as `DocumentTextExtractor` and `DocumentSummarizer`, while the infrastructure layer contains the actual implementations.

This keeps the core application logic independent from the technologies used to implement it and makes the different parts of the application easier to test and maintain.

## Design Decisions

### PostgreSQL for Document Storage

The uploaded document content is stored directly in PostgreSQL together with its metadata.

This keeps the solution simple and allows the original document and its metadata to be managed in the same database.

### Liquibase for Database Migrations

Liquibase is used to manage database schema changes.

This keeps the database structure version controlled and ensures that schema changes are applied consistently.

### Interfaces Between Application and Infrastructure

The application layer communicates with infrastructure through interfaces.

This prevents the application logic from becoming tightly coupled to specific technologies such as Spring Data, Ollama, PDFBox, or Apache POI.

As a result, the implementations can be changed without requiring changes to the core application logic.
