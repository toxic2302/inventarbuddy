FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
ARG MAVEN_PROFILE=Stage
RUN ./mvnw -B -q clean package -DskipTests -P${MAVEN_PROFILE}

FROM eclipse-temurin:25-jre-noble
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /workspace/target/inventarbuddy.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
