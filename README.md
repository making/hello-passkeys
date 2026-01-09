# Spring Security Passkeys Demo

https://github.com/user-attachments/assets/9968db89-7434-41c9-95cb-3a26023bae2a

A quick demonstration project for testing Spring Security's passkey authentication feature.

## Overview

This project demonstrates how to implement WebAuthn-based passkey authentication using Spring Security. It provides the simplest web application where users can register and authenticate using passkeys, with WebAuthn-related data stored in PostgreSQL.

## Documentation

For detailed information about Spring Security's passkey implementation, see:
https://docs.spring.io/spring-security/reference/servlet/authentication/passkeys.html

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose (for PostgreSQL)

## Getting Started

### 1. Start the Application

Run the following command to start the application. PostgreSQL will be automatically started via Docker Compose:

```bash
./mvnw spring-boot:run
```

### 2. Access the Application

Navigate to http://localhost:8080 in your web browser.

### 3. Initial Login

First, sign in with the default credentials:
- Username: `user@example.com`
- Password: `password`

Note: These credentials can be customized via the `spring.security.user.name` and `spring.security.user.password` properties in `application.properties`.

### 4. Register a Passkey

After successful login:
1. Navigate to http://localhost:8080/passkeys
2. Follow your browser's prompts to register a passkey

### 5. Test Passkey Authentication

1. Log out by visiting http://localhost:8080/logout
2. Return to http://localhost:8080
3. Click "Sign in with a passkey"
4. Authenticate using the passkey you registered
