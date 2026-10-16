---
apply: always
---

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
│       ├── storage/               # Storage-Modul (hierarchische Lagerorte)
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
