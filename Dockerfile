# ==============================================================
# = Stage 1: Build
# ==============================================================
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Dependencies first (better layer caching)
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
RUN chmod +x mvnw && ./mvnw -B -q -o dependency:go-offline || ./mvnw -B -q dependency:go-offline

# Application sources
COPY src src
RUN ./mvnw -B -q clean package -DskipTests

# ==============================================================
# = Stage 2: Runtime
# ==============================================================
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN groupadd --system inventarbuddy && useradd --system --gid inventarbuddy --shell /usr/sbin/nologin inventarbuddy

COPY --from=build /workspace/target/inventarbuddy.jar app.jar
RUN chown inventarbuddy:inventarbuddy app.jar

USER inventarbuddy

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
