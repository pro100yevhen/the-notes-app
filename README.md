# The Notes App

A simple Spring Boot application for managing notes.

## Features

- Create, Read, Update, and Delete notes.
- View word frequency statistics for each note.
- API documentation via Swagger/OpenAPI at `/swagger-ui.html`.

## Prerequisites

- Java 21
- Maven 3.9 or later
- Docker and Docker Compose

## How to Run

There are two primary ways to run the application.

### Option 1: Using Docker (Recommended)

This is the easiest way to get started as it runs the application and the MongoDB database in containers.

1.  **Build the application**

    This command compiles the code and creates a `.jar` file in the `target/` directory.
    ```bash
    mvn clean install
    ```

2.  **Start the application**

    Navigate to the `docker` directory and use Docker Compose to build and start the services.
    ```bash
    cd docker
    docker-compose up --build
    ```

3.  **Access the application**

    The API will be available at `http://localhost:8080`.

### Option 2: Running Locally with Maven

This method is useful for active development and debugging.

1.  **Start the database**

    You need a running MongoDB instance. You can use the `docker-compose.yml` file to start one easily.
    ```bash
    docker-compose -f docker/docker-compose.yml up -d mongodb
    ```

2.  **Run the application**

    Use the Spring Boot Maven plugin to run the application.
    ```bash
    mvn spring-boot:run
    ```

3.  **Access the application**

    The API will be available at `http://localhost:8080`.

## How to Run Tests

To run the full suite of unit and integration tests, run the following command from the project root:

```bash
mvn test
```
