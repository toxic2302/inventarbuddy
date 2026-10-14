---
apply: always
---

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
- **Variablen** alles was final kann, soll final werden

### Datenbank

- **DDL-Modus**: `validate` – Hibernate validiert nur das Schema, erstellt oder ändert es nicht.
- **Migrationen**: Liquibase verwaltet alle Schema-Änderungen unter `src/main/resources/db/`.
  - Master-Changelog: `db/db.changelog-master.xml`
  - Changesets liegen in `db/changelog/` organisiert nach Nummern (z.B. `06/01-init-changelog.sql`).
- **Naming-Strategy**: Benutzerdefiniert über `de.toxic2302.inventarbuddy.base.config.NamingStrategy`.
