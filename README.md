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
- `stage`, `prod`: Profiles for the self-deployed environments.

To run with a specific profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=stage
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

## Self-Hosted Deployment (stage & prod)

The application can be built as a Docker image and self-deployed on your own server. A single
image is used for both environments; the active profile is selected at runtime via the
`SPRING_PROFILES_ACTIVE` environment variable.

### 1. Build & Push the Image

The GitHub Actions pipeline (`.github/workflows/maven.yml`) builds and pushes the image to
GitHub Container Registry (`ghcr.io`) automatically:
- Push to `stage` branch &rarr; tag `stage`
- Push to `main`/`master` branch &rarr; tag `prod`

To build it manually:
```bash
docker build -t ghcr.io/<owner>/inventarbuddy:stage .
```

### 2. Configure the Server

Copy `docker-compose.prod.yaml` plus `.docker/nginx` onto the server and create an environment
file from the provided examples:
```bash
cp .env.stage.example .env.stage   # or .env.prod.example -> .env.prod
```
Fill in the real database, Vault, Keycloak and domain values.

### 3. Start the Stack

```bash
docker compose -f docker-compose.prod.yaml --env-file .env.stage up -d
```

`docker-compose.prod.yaml` starts the application container together with an `nginx` reverse
proxy that terminates TLS and a `certbot` sidecar that issues/renews the Let's Encrypt
certificate for the configured `DOMAIN`. On the very first start, request the initial
certificate once nginx is reachable on port 80:
```bash
docker compose -f docker-compose.prod.yaml --env-file .env.stage run --rm certbot certonly \
  --webroot -w /var/www/certbot -d <domain> --email <mail> --agree-tos --no-eff-email
```

### 4. Rolling Out Updates

```bash
docker compose -f docker-compose.prod.yaml --env-file .env.prod pull app
docker compose -f docker-compose.prod.yaml --env-file .env.prod up -d app
```

### 5. CI/CD (GitHub Actions)

`.github/workflows/maven.yml` automatically builds & pushes the image after a successful test run
and (if configured) deploys it via SSH. For automatic deployment, configure the following
repository secrets (per GitHub `stage`/`prod` [Environment](https://docs.github.com/en/actions/deployment/targeting-different-environments/using-environments-for-deployment)):
- `DEPLOY_HOST` – SSH host of the server
- `DEPLOY_USER` – SSH user
- `DEPLOY_SSH_KEY` – private SSH key with access to the server
- `DEPLOY_PATH` – path on the server containing `docker-compose.prod.yaml` and the matching `.env.stage`/`.env.prod`

Without these secrets configured, the deploy job will simply fail and the image still gets
published to `ghcr.io`, so a manual rollout (see step 4) is always possible.
