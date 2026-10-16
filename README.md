[![Java CI with Maven](https://github.com/toxic2302/inventarbuddy/actions/workflows/maven.yml/badge.svg)](https://github.com/toxic2302/inventarbuddy/actions/workflows/maven.yml)
![Coverage](https://raw.githubusercontent.com/toxic2302/inventarbuddy/badges/.github/badges/jacoco.svg)
![Branch Coverage](https://raw.githubusercontent.com/toxic2302/inventarbuddy/badges/.github/badges/branches.svg)

# Inventarbuddy

Inventarbuddy is a Spring Boot application designed for inventory management. It provides a robust backend with integrated secret management, database migrations, and API documentation.

![Static Badge](https://img.shields.io/badge/springboot-logo?style=for-the-badge&logo=springboot&labelColor=black)
![Static Badge](https://img.shields.io/badge/postgresql-logo?style=for-the-badge&logo=postgresql&labelColor=white)
![Static Badge](https://img.shields.io/badge/keycloak-logo?style=for-the-badge&logo=keycloak&labelColor=black)
![Static Badge](https://img.shields.io/badge/liquibase-logo?style=for-the-badge&logo=liquibase&labelColor=black)
![Static Badge](https://img.shields.io/badge/hashicorp-logo?style=for-the-badge&logo=hashicorp&labelColor=black)
![Static Badge](https://img.shields.io/badge/docker-logo?style=for-the-badge&logo=docker&labelColor=black)
![Static Badge](https://img.shields.io/badge/apachemaven-logo?style=for-the-badge&logo=apachemaven&labelColor=black)
![Static Badge](https://img.shields.io/badge/swagger-logo?style=for-the-badge&logo=swagger&labelColor=black)

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

The project includes a `docker-compose.local.yaml` file to set up the necessary services (PostgreSQL, Vault, Adminer).

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

## Deployment with seelf

The backend can be deployed as **stage** deployment with [seelf](https://yuukanoo.github.io/seelf/). seelf picks up `docker-compose.stage.yaml` (preferred over `docker-compose.local.yaml`, which only contains the local infrastructure), builds the `Dockerfile` (Maven profile `Stage`, Spring profile `stage`) and exposes the service with the published port `8080`.

1. Create a target in seelf with the URL `https://inventarbuddy.de` (certificates are issued automatically because of `https`).
2. Create an application named `stage` pointing to this repository (or upload the project as archive). The application name determines the subdomain, so the backend is served at `https://stage.inventarbuddy.de`.
3. Set the following environment variables on the application:
   - `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`
   - `KEYCLOAK_ISSUER_URI` (e.g. `https://auth.example.com/realms/inventarbuddy`)
   - `VAULT_HOST`, `VAULT_ROLE_ID`, `VAULT_SECRET_ID` (optional: `VAULT_PORT`, `VAULT_SCHEME`)
4. Make sure a DNS record for `stage.inventarbuddy.de` (or a wildcard `*.inventarbuddy.de`) points to the seelf server, then deploy. The API is available at `https://stage.inventarbuddy.de/inventarbuddy`.

To deploy another stage, override the build arg `MAVEN_PROFILE` in `docker-compose.stage.yaml` (e.g. `Prod`).

PostgreSQL, Keycloak and Vault must be reachable from the container (they are not part of the seelf deployment).

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
