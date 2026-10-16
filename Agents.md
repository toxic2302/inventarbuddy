# Agents.md – Inventarbuddy

## Projektübersicht

**Inventarbuddy** ist eine Spring Boot Anwendung zur Inventarverwaltung. Die Anwendung bietet eine REST-API, die durch Keycloak (OAuth2) abgesichert ist.

## Tech-Stack

| Technologie              | Version / Details                  |
|--------------------------|------------------------------------|
| Java                     | 25                                 |
| Spring Boot              | 4.1.0                              |
| Spring Cloud             | 2025.1.2                           |
| Datenbank                | PostgreSQL (42.7.11)               |
| ORM                      | Spring Data JPA / Hibernate        |
| DB-Migrationen           | Liquibase                          |
| Authentifizierung        | Keycloak (OAuth2 Resource Server)  |
| Secret Management        | HashiCorp Vault (AppRole)          |
| Mapping                  | MapStruct 1.6.3                    |
| Boilerplate-Reduktion    | Lombok                             |
| API-Dokumentation        | SpringDoc OpenAPI (Swagger) 3.0.3  |
| Test-Datenbank           | H2 (In-Memory)                     |
| Code Coverage            | JaCoCo 0.8.14                      |
| Build-Tool               | Maven (mit Maven Wrapper)          |

## Projektstruktur

```
src/main/java/de/toxic2302/inventarbuddy/
├── base/                          # Basis-Infrastruktur (gemeinsam genutzt)
│   ├── authentication/            # Authentifizierungs-Services (z.B. AuthenticatedUserService)
│   ├── config/                    # Konfigurationen
│   │   └── security/              # Security-Konfiguration
│   │       └── local/             # Lokale Security-Overrides
│   ├── entity/                    # Basis-Entitäten (z.B. BaseEntity)
│   ├── error/                     # Fehlerklassen
│   ├── handler/                   # Exception-Handler
│   ├── mapper/                    # Basis-Mapper
│   └── service/                   # Basis-Services
├── core/
│   └── modules/                   # Fachmodule
│       ├── category/              # Kategorie-Modul
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── mapper/
│       │   ├── repository/
│       │   └── service/
│       ├── item/                  # Item-Modul
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── mapper/
│       │   ├── repository/
│       │   └── service/
│       ├── media/                 # Media-Modul (Bilder und PDFs, mit Item verknüpft)
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── mapper/
│       │   ├── repository/
│       │   └── service/
│       └── user/                  # User-Modul
│           ├── entity/
│           ├── repository/
│           └── service/
└── external/                      # Externe Integrationen
```

## Architektur-Konventionen

### Modulstruktur

Jedes Fachmodul unter `core/modules/` folgt einer einheitlichen Schichtenarchitektur:

- **Controller** – REST-Endpunkte
- **DTO** – Data Transfer Objects für API-Request/Response
- **Entity** – JPA-Entitäten (erben von `BaseEntity`)
- **Mapper** – MapStruct-Mapper für Entity ↔ DTO Konvertierung
- **Repository** – Spring Data JPA Repositories
- **Service** – Geschäftslogik

### Code-Stil

- **Lombok** wird für Getter, Setter und Konstruktoren verwendet (`@Getter`, `@Setter`, `@RequiredArgsConstructor`).
- **Entitäten** erben von `BaseEntity` und verwenden JPA-Annotationen (`@Entity`, `@Table`, `@Column`).
- **Spaltennamen** in `@Column` verwenden camelCase (z.B. `keycloakId`, `firstName`).
- **Tabellennamen** werden in `@Table(name = "...")` explizit angegeben.
- **MapStruct** wird zusammen mit Lombok über `lombok-mapstruct-binding` verwendet.
- **Kommentare** im Code sind selten – Code soll selbstdokumentierend sein.

### Datenbank

- **DDL-Modus**: `validate` – Hibernate validiert nur das Schema, erstellt oder ändert es nicht.
- **Migrationen**: Liquibase verwaltet alle Schema-Änderungen unter `src/main/resources/db/`.
  - Master-Changelog: `db/db.changelog-master.xml`
  - Changesets liegen in `db/changelog/`. Die Unterordner sind der **Monat** (zweistellig, z.B. `10`), in dem die Änderung erstellt wird; die Dateien darin werden fortlaufend nummeriert (z.B. `10/01-add-parent-to-categories-table.sql`, danach `10/02-add-items-table.sql`).
  - Für **neue Entitäten, die noch nicht im System (z.B. in Produktion) sind**, wird kein zusätzlicher Changelog angelegt, sondern der vorhandene Changelog der Entität angepasst. Neue Changesets (`ALTER TABLE`) sind nur für bereits ausgerollte Tabellen nötig; bereits ausgerollte Changesets dürfen nicht verändert werden (Checksum-Fehler).
- **Naming-Strategy**: Benutzerdefiniert über `de.toxic2302.inventarbuddy.base.config.NamingStrategy`.

## Build & Run

### Voraussetzungen

- Java 25
- Maven (oder `./mvnw` verwenden)
- Docker (für lokale Infrastruktur via `docker-compose.local.yaml`)
- Umgebungsvariablen: siehe `local.env`

### Befehle

```bash
# Build
./mvnw clean package

# Tests ausführen
./mvnw test

# Lokal starten (Profil: local)
./mvnw spring-boot:run
```

### Profile

| Profil  | Beschreibung                     |
|---------|----------------------------------|
| `local` | Lokale Entwicklung (Standard)    |
| `dev`   | Entwicklungsumgebung             |
| `stage` | Staging-Umgebung                 |
| `prod`  | Produktionsumgebung              |

## API

- **Basis-Pfad**: `/inventarbuddy`
- **API-Spezifikation**: `OpenApiSpec.yaml` im Projektstamm
- **Swagger UI** (lokal): verfügbar über SpringDoc OpenAPI

## Tests

- **Framework**: JUnit 5 / Spring Boot Test
- **Test-Datenbank**: H2 (In-Memory)
- **Security-Tests**: `spring-security-test`
- **Code Coverage**: JaCoCo (Ausschlüsse: `InventarbuddyApplication`, `base/entity`, `base/mapper`, `base/service`, `base/exceptions`, `config`)

## Wichtige Hinweise für Agenten

1. **Keine Schema-Änderungen via Hibernate** – Alle DB-Änderungen müssen als Liquibase-Changesets angelegt werden.
   - Der Changelog-Unterordner entspricht dem aktuellen Monat; die Dateinummer ist die nächste freie im Ordner (z.B. `10/02-...`).
   - Entitäten, die noch nicht im System sind, werden im vorhandenen Changelog angepasst – kein neuer Changelog.
2. **Entitäten immer von `BaseEntity` erben lassen.**
3. **MapStruct für Mappings verwenden** – keine manuellen Mapper schreiben.
4. **Lombok für Boilerplate nutzen** – Getter/Setter/Konstruktoren nicht manuell anlegen.
5. **Tests mit H2** – Produktionsdatenbank ist PostgreSQL, Tests laufen gegen H2.
6. **Security beachten** – Alle Endpunkte sind standardmäßig durch OAuth2/Keycloak geschützt.
7. **Vault für Secrets** – Sensible Konfigurationswerte werden über HashiCorp Vault bereitgestellt.
