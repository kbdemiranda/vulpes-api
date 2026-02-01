# Vulpes API

Backend **reference project** built with **Java 21 and Spring Boot 3**, focused on
**clean architecture, domain modeling and production-ready backend patterns**.

This project does **not aim to be a full commercial product**, but rather a
well-structured backend showcasing **architectural decisions, best practices
and real-world concerns** commonly found in regulated and large-scale systems.

---

## 🎯 Project Goals

- Demonstrate clean backend architecture using **modern Java (21)**
- Apply domain-driven design principles
- Showcase security, data integrity and scalability concerns
- Serve as a technical reference for **Spring Boot 3** backend projects

---

## 🏗️ Architecture Overview

The project follows a **layered, domain-oriented architecture**, clearly
separating responsibilities:

- **API Layer**  
  REST controllers, request/response mapping and validation

- **Application Layer**  
  Use cases, orchestration logic and transactional boundaries

- **Domain Layer**  
  Core business rules, entities and domain services

- **Infrastructure Layer**  
  Persistence, security, external integrations and framework-specific concerns

This separation enables high testability, low coupling and clear business
boundaries.

---

## 🧠 Key Technical Decisions

- **Java 21** for long-term support and modern language features
- **Spring Boot 3.5.x** for ecosystem maturity and productivity
- **Spring Security** with stateless authentication using **JWT**
- **PostgreSQL** as the primary relational database
- **JPA / Hibernate** for persistence abstraction
- **Flyway** for database versioning and schema evolution
- **DTO mapping** using ModelMapper to isolate domain from transport layer
- **Lombok** to reduce boilerplate while keeping the domain expressive
- **Actuator** for application health and metrics exposure

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Security
- JWT (java-jwt)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway
- ModelMapper
- Lombok
- Spring Validation
- Spring Actuator
- OpenAPI / Swagger (springdoc)

---

## 🔐 Security Model

- Stateless authentication using **JWT**
- Role-based access control
- Security configuration isolated from business logic
- API boundaries protected via Spring Security filters

---

## 🗄️ Domain Overview

The domain models a **subscription management context**, including:

- Subscribers
- Platforms
- Payments
- User access and roles
- Monthly payment status

The focus is on **business consistency and domain rules**, not pure CRUD.

---

## 🚀 Running the Project Locally

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Steps
```bash
mvn clean install
java -jar target/vulpes-0.0.1-SNAPSHOT.jar
