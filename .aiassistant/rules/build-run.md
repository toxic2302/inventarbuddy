---
apply: always
---

## Build & Run

### Voraussetzungen

- Java 25
- Maven (oder `./mvnw` verwenden)
- Docker (für lokale Infrastruktur via `../../docker-compose.local.yaml`)
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
