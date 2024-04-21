# Messaging Application

This is a Messaging Application backend developed using Spring Boot, PostgreSQL, and RabbitMQ. It provides an HTTP API for creating user accounts, sending messages, and viewing received and sent messages.

## Features

- Create user accounts with unique nicknames.
- Send messages to other users.
- View all received and sent messages.
- View all messages received from a particular user.
- Bonus: Sending a message puts a message on a queue using RabbitMQ.

## Requirements

- Docker
- Docker Compose

## Running the Application

To run the Messaging Application, follow these steps:

1. Clone this repository:

   ```bash
   git clone https://github.com/SohrabRahmani/simplified-messaging-system.git
   
2. Navigate to the project directory:
   cd simplified-messaging-system

3. Run the following command to start the application:
   docker-compose up

This command will build the Docker images for the application, PostgreSQL, and RabbitMQ, and start the containers.
Once the containers are up and running, you can access the Messaging Application at http://localhost:8080.

## API Endpoints
The Messaging Application exposes the following HTTP endpoints:

- `POST /api/user`: Create a new user account.
- `POST /api/message`: Send a message to another user.
- `GET /api/message/received`: View all messages received.
- `GET /api/message/sent`: View all messages sent.
- `GET /api/message/from/{userId}`: View all messages received from a particular user.

## Swagger Documentation
The API documentation for the Messaging Application is available using Swagger. 
After starting the application, you can access the Swagger UI at http://localhost:8080/swagger-ui/index.html. 
This UI provides detailed documentation of all available endpoints, request parameters, and response schemas.

## Environment Variables
The following environment variables are available for configuration:

- `DB_URL`: PostgreSQL database URL.
- `DB_NAME`: PostgreSQL database name.
- `DB_USERNAME`: PostgreSQL database username.
- `DB_PASSWORD`: PostgreSQL database password.
- `RABBITMQ_HOST`: RabbitMQ hostname.
- `RABBITMQ_PORT`: RabbitMQ port.
- `RABBITMQ_USERNAME`: RabbitMQ username.
- `RABBITMQ_PASSWORD`: RabbitMQ password.
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: Hibernate DDL auto configuration.

## Improvements

Here are some potential improvements to consider:

### Scalability:
- Consider implementing mechanisms to horizontally scale consumer components to handle increased message loads. This will ensure the application remains responsive under heavy traffic conditions.

### Error Handling:
- Enhance error handling in consumer components to handle failures gracefully. Implement mechanisms such as logging errors for later analysis and monitoring.

### Testing:
- Consider implementing integration tests to validate the end-to-end behavior of the messaging system. These tests will verify the interaction between different components of the application, including the producer, consumer, and messaging queue.
