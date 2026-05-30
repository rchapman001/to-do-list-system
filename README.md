# To-Do List System

## Table of Contents

- [Overview](#overview)
- [Installation](#installation-and-set-up)
- [Usage](#usage)
- [Testing](#testing)
- [Code Formatting](#code-formatting)

## Overview

This To-Do List system allows you to create and manage lists and tasks while also displaying the
weather in Minneapolis. I built this app to practice developing and testing Java Spring Boot
microservices.

[Requirements](./docs/requirements.md)

[Architecture](./docs/architecture-diagram.drawio.svg)

## Installation and Set Up

1. Prerequisites: Before you begin, ensure you have the following installed on your machine:
    - [Visual Studio Code](https://code.visualstudio.com/download): This app is set up for VS Code,
      but you may use a different editor if you would like.
    - [Java 17](https://www.oracle.com/java/technologies/downloads/#java17): To verify if Java is
      installed, run the following command and you should see java 17 returned.
        ```bash
        java --version
        ```
    - [Git](https://git-scm.com/downloads/): To check if Git is installed, run the command below. If
      it is installed correctly you'll see its version number. 
        ```bash
        git --version
        ```
    - [Docker Desktop](https://www.docker.com/get-started/): To check if Docker is installed, run
      the command below. If it is installed correctly you'll see its version number.
        ```bash
        docker version
        ```
    - [Postman](https://www.postman.com/downloads/): Verify Postman was installed on your computer.

2. Clone Repository: 
    - Open a terminal or command prompt and run the following command to clone the repository:
        ```bash
        git clone https://github.com/rchapman001/to-do-list-system.git
        ```
      
3. Setup VS Code: 
    - Install the VS Code Extensions: Navigate to extensions.json file in .vscode folder and install
      all extensions listed in the file.
    - Set Java home path: Navigate to settings.json file in .vscode folder and set the
      `"java.jdt.ls.java.home"` setting to the correct path of your JDK installation.

4. Configure `host.docker.internal`: This configuration allows your frontend application to access
   backend services running in Docker containers using the URL. `http://host.docker.internal:<port>`
    - For Mac and Windows:
        - Open a terminal and edit the `/etc/hosts` file using a text editor with sudo access, such
          as nano or vim:
            ```bash
            sudo nano /etc/hosts
            ```
        - Add the following line to the file:
            ```txt
            127.0.0.1 host.docker.internal
            ```
        - Save and close the file.

## Usage

- Open Docker Desktop: To run the app you need to have docker desktop open. 

- Local Development Environment (Database in Docker Only):
  - How to launch the app:
    - With the Docker extension installed, right click the docker-compose.yml file and select
      'Compose up - Select Services' then select the to-do-list-db. This will start the database
      service only, including creating the schema and setting up some test data. Alternatively, you
      can run the following command in the directory where the docker-compose.yml file is located:
      ```bash
      docker compose up -d to-do-list-db
      ```
    - In the Spring Boot Dashboard, run the services you are developing and testing. Alternatively,
      you can navigate to each app individually in a separate terminal and run the following
      command: 
      ```bash
      ./mvnw spring-boot:run
      ```
  - How to shut down the app:
    - With the Docker extension installed, right click the docker-compose.yml file and select
      'Compose Down'. Alternatively, you can run the following command in the directory where the
      docker-compose.yml file is located:
      ```bash
      docker compose down to-do-list-db
      ```
    - In the Spring Boot Dashboard, click the stop button to stop all apps. Alternatively, you can
      type Ctrl + C to kill each app in their respective terminal.

- Local Development Environment (Fully Dockerized):
  - How to launch the app: With the Docker extension installed, right click the docker-compose.yml
    file and select 'Compose up'. This will start all the services in docker containers. Including
    setting up the database schema and some test data. Alternatively, you can run the following
    command in the directory where the docker-compose.yml file is located:
    ```bash
    docker compose up -d
    ```
  - How to stop the app: With the Docker extension installed, right click the docker-compose.yml
    file and select 'Compose Down'. Alternatively, you can run the following command in the
    directory where the docker-compose.yml file is located:
    ```bash
    docker compose down
    ```

- Web UI: With all services running, you can access the app by pasting the following URLs into your
  web browser:
  - http://localhost:8080/to-do-list/test
  - http://localhost:8080/to-do-list/home

- Postman: With the backend services and their dependencies running. Open Postman and import the
  Postman collection located here:
  `docs/to-do-list-system.json` to interact with the
  backend using the predefined requests.

- PSQL: With the Postgres Docker service running, you can interact with the database using PSQL in a
  terminal.
  - In the Docker Extension right click the postgres container that you want to run a psql command.
    Then Click "Attach Shell".
  - Run "psql --version" to verify you can run psql commands. If this works you can run and psql
    command.
  - Run this command to connect to the corresponding database: `psql -U to_do_list_db_service_user
    -d to_do_list_db`
  - Run SQL Statements (don't forget semicolon). For example: `SELECT * FROM to_do_list;`

- PGAdmin: With the Postgres Docker and the PGAdmin services running you can interact with the
  database using PGAdmin.
  - Open browser and navigate to: http://localhost:5050. May take a little to load.
  - Login to PGAdmin using account specified in docker-compose.yml
      - Email / Username: admin@localhost.com
      - Password: admin
  - If it's the first time using PGAdmin or you deleted the containers and started them again you
    need to add the server. To do this. Right click "Server" -> click "Register" -> click
    "Server...". 
      - Under "General":
          - Name: Can be anything
      - Under "Connection":
          - Host name/address. To find the ip address of your database. Open a terminal and running
            `docker ps` which lists all the containers that you are running. Copy the CONTAINER ID
            of the container running the postgress database. Then run this command: `docker inspect
            0986a4efeccf | grep IPAddress`. 
          - Maintenance database: POSTGRES_DB from the docker-compose.yml under the corresponding
            postgres service.
          - Port: 5432
          - Username: POSTGRES_USER from the docker-compose.yml under the corresponding postgres
            service.
          - password: POSTGRES_PASSWORD from the docker-compose.yml under the corresponding postgres
            service.
          - Click "Save"
  - If the connection was successful. Click "Servers" -> click "<database-name>" -> click
    "Databases" -> lick "<database-name>" -> click "Schemas" -> click "Tables". Then you should see
    the tables that were created as apart of the Spring Boot app.

## Testing

- How to Run Tests: 
  - Open Docker Desktop: For the test containers to work you need to have docker desktop open. 
  - With the Java Test extension for VS Code, you are able to run the tests from the Testing panel
    or in each test classes. To use the Testing panel, open the Testing panel then click the run
    tests button (there are a few options depending on which tests you want to run). You can also
    open a specific test class and run the tests by clicking the "run" button next to the test class
    or a specific test method.
  - Alternatively, you can run the tests using the mvn wrapper. To run the tests for a specific app
    you can navigate to that app then run this command: 
    ```bash
    ./mvnw test
    ```
    To run tests for multiple apps at once, use a command like this:
    ```bash
    (cd list-service && ./mvnw test) && (cd task-service && ./mvnw test)
    ```

## Code Formatting

1. [spotless](https://github.com/diffplug/spotless) is used to format java code. To setup Spotless
   for a Spring Boot app, just add the plugin to the pom.xml. For example,
    ```xml
        <!-- Spotless plugin configuration -->
        <plugin>
          <groupId>com.diffplug.spotless</groupId>
          <artifactId>spotless-maven-plugin</artifactId>
          <configuration>
            <java>
              <includes>
                <include>src/main/java/**/*.java</include> <!-- Check application code -->
                <include>src/test/java/**/*.java</include> <!-- Check application tests code -->
              </includes>
              <googleJavaFormat>
                <style>GOOGLE</style>
              </googleJavaFormat>
            </java>
          </configuration>
        </plugin>
    ```
  - Once the plugin is added to the pom.xml, you can use the following command to check the
    formatting for each service and stop if any violations are found.
    ```bash
    (cd list-service && ./mvnw spotless:check) && (cd task-service && ./mvnw spotless:check) && (cd to-do-list-ui && ./mvnw spotless:check) && (cd weather-service && ./mvnw spotless:check)
    ```
  - Then uses this command to apply the formatting to each app.
    ```bash
    (cd list-service && ./mvnw spotless:apply) && (cd task-service && ./mvnw spotless:apply) && (cd to-do-list-ui && ./mvnw spotless:apply) && (cd weather-service && ./mvnw spotless:apply)
    ```
  - If you want to automatically format your Java code using Spotless before committing, follow
    these steps.
    - Set the custom Git hooks path: Run the following command to configure Git to use the .githooks
      directory for hooks.
      ```bash
      git config core.hooksPath .githooks
      ```
    - Make the pre-commit script executable: Ensure that the pre-commit hook script in the .githooks
      directory is executable.
      ```bash
      chmod +x .githooks/pre-commit
      ```

2. [rewrap](https://github.com/staabm/vscode-rewrap) is a Visual Studio Code extension that wraps
   comments and other text to a specified line length. To use rewrap in VS Code:
  - Install the rewrap extension from the Visual Studio Code Marketplace.
  - Set the desired line length for wrapping comments in your `settings.json`:
    ```json
    {
        "editor.wordWrap": "wordWrapColumn", // Rewrap setting
        "editor.wordWrapColumn": 88, // Rewrap setting
        "editor.rulers": [88], // Rewrap setting
    }
    ```
  - With your cursor in the comment block, press the appropriate keyboard shortcut (usually Alt+Q or
    Alt+Shift+Q) to rewrap the comment to the specified line length.