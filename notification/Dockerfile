FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Копируем весь проект целиком
COPY . .

# Отладка: показываем структуру
RUN ls -la && \
    ls -la common-dtos/ && \
    ls -la bank_rest/ && \
    ls -la notification/

# Собираем весь проект (не указывая конкретные модули)
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем JAR из директории notification/target
COPY --from=build /app/notification/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]