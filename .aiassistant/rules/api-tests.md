---
apply: always
---

## API

- **Basis-Pfad**: `/inventarbuddy`
- **API-Spezifikation**: `OpenApiSpec.yaml` im Projektstamm
- **Swagger UI** (lokal): verfügbar über SpringDoc OpenAPI

## Tests

- **Framework**: JUnit 5 / Spring Boot Test
- **Test-Datenbank**: H2 (In-Memory)
- **Security-Tests**: `spring-security-test`
- **Code Coverage**: JaCoCo (Ausschlüsse: `InventarbuddyApplication`, `base/entity`, `base/mapper`, `base/service`, `base/exceptions`, `config`)
