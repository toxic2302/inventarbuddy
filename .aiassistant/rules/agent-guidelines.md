---
apply: always
---

## Wichtige Hinweise für Agenten

1. **Keine Schema-Änderungen via Hibernate** – Alle DB-Änderungen müssen als Liquibase-Changesets angelegt werden.
2. **Entitäten immer von `BaseEntity` erben lassen.**
3. **MapStruct für Mappings verwenden** – keine manuellen Mapper schreiben.
4. **Lombok für Boilerplate nutzen** – Getter/Setter/Konstruktoren nicht manuell anlegen.
5. **Tests mit H2** – Produktionsdatenbank ist PostgreSQL, Tests laufen gegen H2.
6. **Security beachten** – Alle Endpunkte sind standardmäßig durch OAuth2/Keycloak geschützt.
7. **Vault für Secrets** – Sensible Konfigurationswerte werden über HashiCorp Vault bereitgestellt.
