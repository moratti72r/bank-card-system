FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY . .

RUN ls -la && \
    ls -la common/ && \
    ls -la bank_rest/ && \
    ls -la audit/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/audit/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]