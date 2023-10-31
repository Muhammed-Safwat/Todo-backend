# To-Do Application (Back end)

## Overview

This Java Spring Boot application is designed to manage to-do lists and provide user authentication with email and password. It also supports social login and registration for user convenience.

## Features

- Create, Read, Update, and Delete (CRUD) operations for to-do items.
- User authentication with email and password.
- Social login and registration using popular social media platforms.
- Secure and easy-to-use API endpoints for managing tasks and user accounts.

## Class Digram
<img src="Todo_App.jpg" alt="Calss digram.....">

## Technology

- **Java 11**: The application is built using Java 11, harnessing its robust features and compatibility.
- **Spring Boot**: The project is based on the Spring Boot framework, providing a strong foundation for building web applications.
  - `spring-boot-starter-data-jpa`: For data access using JPA.
  - `spring-boot-starter-security`: Enabling security features.
  - `spring-boot-starter-web`: Facilitating web application development.

- **OAuth2 Support**: The following dependencies are used for social login:
  - `spring-security-oauth2-client`: Supporting OAuth2 client functionality.
  - `spring-security-oauth2-jose`: Handling OAuth2 JSON Web Tokens (JWT).

- **Database Connectivity**: The application connects to a database using:
  - `mysql-connector-java`: A MySQL JDBC driver for database interaction.

- **API Documentation**: API documentation is made possible with:
  - `springdoc-openapi-starter-webmvc-ui`: Providing OpenAPI documentation and Swagger UI for API exploration.

- **Lombok**: Reducing boilerplate code in Java classes, enhancing code readability.

- **MapStruct**: Simplifying object mapping, allowing easy conversion between data transfer objects (DTOs) and entity objects.

- **Log4j2**: Logging is configured with Log4j2 for effective application log management.

- **Spring Boot DevTools**: Streamlining development with automatic application restarts during development.

- **Spring Boot Starter Mail**: Enabling email functionality for features such as password reset and email notifications.

- **Spring Boot Starter Validation**: Supporting input and data validation for improved data integrity.

- **JJWT (Java JWT)**: Handling JSON Web Tokens in Java applications, facilitated by the following dependencies:
  - `jjwt-api`, `jjwt-impl`, `jjwt-jackson`.

These primary technologies and dependencies form the core of the application, enabling the development of a secure and feature-rich to-do application with user authentication and social login capabilities.

