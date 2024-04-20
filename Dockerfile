FROM openjdk:21-slim as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/target/messaging-0.0.1-SNAPSHOT.jar /app/
CMD ["java", "--enable-preview", "-jar", "/app/messaging-0.0.1-SNAPSHOT.jar"]

ENV DB_URL=postgres
ENV DB_NAME=messaging
ENV DB_USERNAME=messaging_user
ENV DB_PASSWORD=messaging_password
ENV RABBITMQ_HOST=rabbitmq
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USERNAME=guest
ENV RABBITMQ_PASSWORD=guest
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect