# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Building and Running
- Build the project: `mvn clean install`
- Run the application: `java -jar target/vulpes-0.0.1-SNAPSHOT.jar`
- Start with Docker: `docker-compose up`
- Run with Maven: `mvn spring-boot:run`

### Testing
- Run all tests: `mvn test`
- Run a specific test class: `mvn test -Dtest=AssinanteServiceTest`
- Run tests with coverage: `mvn clean test jacoco:report`

### Database
- Database migrations are handled by Flyway in `src/main/resources/db/migration/`
- Default database connection: `postgresql://localhost:5432/dev` (schema: vulpes)
- Initialize database: `docker-compose up db` or run `init.sql`

## Project Architecture

### Core Structure
This is a Spring Boot subscription management API (Vulpes) following a clean architecture pattern:

- **Applications Layer** (`src/main/java/io/github/vulpes/applications/`):
  - Controllers: REST endpoints for API
  - DTOs: Data transfer objects for external communication
  - Services: Business logic interfaces and implementations (in `impl/` subpackage)

- **Domain Layer** (`src/main/java/io/github/vulpes/domain/`):
  - Models: Core entities (Assinante, Plataforma, Pagamento, Usuario, etc.)
  - Enums: Domain-specific enumerations (TipoServico)

- **Infrastructure Layer** (`src/main/java/io/github/vulpes/infrastructure/`):
  - JPA repositories for data access
  - Security configuration with JWT authentication
  - Exception handling with GlobalExceptionHandler
  - Database configuration

### Key Patterns
- Service interfaces with implementation classes in `impl/` subdirectories
- ModelMapper for entity-DTO conversions
- Repository pattern with Spring Data JPA
- JWT-based authentication with Spring Security
- Custom exception handling with VulpesException

### Authentication & Security
- JWT tokens required for most endpoints (except login, user registration, and documentation)
- Context path: `/api`
- Public endpoints: `/auth/login`, `/auth/logout`, `POST /usuarios`, Swagger docs
- CORS configuration supports configurable origins, methods, and headers
- Security configuration in `SecurityConfigurations.java:39-50`

### Database Schema
Uses PostgreSQL with Flyway migrations in the `vulpes` schema:
- Core entities: Assinante, Plataforma, Pagamento, Usuario, Perfil
- Junction table: AssinantePlataforma (many-to-many relationship)
- Status tracking: StatusPagamentoMensal

### Testing Approach
- JUnit 5 with Mockito for unit tests
- Test classes follow naming convention: `[ServiceName]Test`
- Tests located in `src/test/java/` mirroring main package structure
- MockitoAnnotations.openMocks() pattern in test setup