# Etapa 1: Build da aplicação
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final para execução
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o JAR compilado da etapa anterior
COPY --from=build /app/target/produtos-*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
