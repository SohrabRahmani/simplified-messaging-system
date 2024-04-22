FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/messaging-0.0.1-SNAPSHOT.jar /app/
CMD ["java", "--enable-preview", "-jar", "/app/messaging-0.0.1-SNAPSHOT.jar"]

ENV DB_URL=postgres
ENV DB_NAME=messaging_db
ENV DB_USERNAME=messaging_user
ENV DB_PASSWORD=messaging_password
ENV RABBITMQ_HOST=rabbitmq
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USERNAME=guest
ENV RABBITMQ_PASSWORD=guest
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect