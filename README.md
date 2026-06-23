# Inventarbuddy

Inventarbuddy is a Spring Boot application designed for inventory management. It provides a robust backend with integrated secret management, database migrations, and API documentation.

## Tech Stack

- **Java:** 25
- **Framework:** Spring Boot 4.1.0
- **Database:** PostgreSQL
- **Migrations:** Liquibase
- **Secret Management:** Hashicorp Vault
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Other:** Lombok, MapStruct, Maven

## Prerequisites

Before you begin, ensure you have the following installed:
- [Java 25 JDK](https://openjdk.org/)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/) and Docker Compose

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd inventarbuddy
```

### 2. Start Infrastructure

The project includes a `docker-compose.yaml` file to set up the necessary services (PostgreSQL, Vault, Adminer).

```bash
docker-compose up -d
```

- **PostgreSQL:** `localhost:5432` (User: `dev`, Pass: `pass`, DB: `inventarbuddy`)
- **Hashicorp Vault:** `localhost:8200`
- **Adminer (DB UI):** `localhost:8082`

### 3. Configuration

The application uses Spring Cloud Vault to manage secrets. Ensure your local environment is configured to connect to the Vault instance started by Docker.

Default local settings in `application.properties`:
- `spring.mvc.servlet.path=/inventarbuddy`
- `VAULT_HOST=localhost`
- `VAULT_PORT=8200`

### 4. Build and Run

Build the project using Maven:

```bash
./mvnw clean install
```

Run the application:

```bash
./mvnw spring-boot:run
```

## API Documentation

The API is documented using Swagger UI. Due to the configured servlet path, the documentation is available at:

- **Swagger UI:** [http://localhost:8080/inventarbuddy/swagger-ui/index.html](http://localhost:8080/inventarbuddy/swagger-ui/index.html)
- **OpenAPI Docs:** `http://localhost:8080/inventarbuddy/v3/api-docs`

## Development

### Profiles
The application supports different profiles:
- `local`: Default profile for local development.
- `dev`, `stage`, `prod`: Profiles for different environments.

To run with a specific profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Migrations
Liquibase is used for database versioning. Changelogs can be found in:
`src/main/resources/db/changelog`

### Code Quality
JaCoCo is integrated for code coverage reports. You can generate a report by running:
```bash
./mvnw test
```
The report will be available in `target/site/jacoco/index.html`.
