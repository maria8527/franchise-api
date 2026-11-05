# ---------- Etapa 1: compilar ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Primero copiamos lo que casi no cambia, así Docker reutiliza esta capa en caché
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Después el código fuente, que cambia seguido
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ---------- Etapa 2: ejecutar ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuario sin privilegios para no ejecutar la aplicación como root
RUN useradd --system --uid 1001 appuser

COPY --from=build /app/target/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]