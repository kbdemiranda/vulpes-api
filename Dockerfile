# Usar a imagem oficial do OpenJDK
FROM openjdk:11-jre-slim

# Informações sobre o mantenedor
LABEL maintainer="kbdemiranda@hotmail.com"

# Copiar o JAR para o container
COPY target/vulpes-0.0.1-SNAPSHOT.jar /app.jar

# Explicação do que o comando faz
# Executar o JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Explicação do que o comando faz
# Expor a porta 8080 para acessar a aplicação
EXPOSE 8080
