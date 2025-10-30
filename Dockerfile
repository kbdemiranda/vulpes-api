# ========= STAGE 1: BUILD =========
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

# Código-fonte
COPY src ./src
RUN mvn -q -B -DskipTests package

# ========= STAGE 2: RUNTIME =========
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia o JAR gerado do build
# (pega o único SNAPSHOT produzido)
COPY --from=builder /app/target/*SNAPSHOT.jar /app/app.jar

# Plataformas como Render/Heroku injetam $PORT — força o Spring a usá-la
ENV JAVA_TOOL_OPTIONS="-Dserver.port=${PORT}"

# Porta padrão local (informativa)
EXPOSE 8080

# Sobe a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
