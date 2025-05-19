# jwt-auth-app

A Spring Boot application providing JWT-based authentication and authorization, with full role and permission management, plus REST APIs for creating stories and comments.

## Table of Contents

* [Features](#features)
* [Prerequisites](#prerequisites)
* [Installation](#installation)
* [Configuration](#configuration)
* [Running the Application](#running-the-application)
* [Frontend](#frontend)
* [API Endpoints](#api-endpoints)

## Features

* **JWT Authentication:** Secure login and token issuance
* **Role Management:** Create roles and assign them to users
* **Permission Management:** Attach predefined permissions to roles
* **User Management:** Register new users and manage credentials
* **Story API:** CRUD operations for stories (`/api/stories`)
* **Comment API:** Add and retrieve comments for stories (`/api/stories/{id}/comments`)

## Prerequisites (Backend)

* **Java:** 21
* **Build Tool:** Maven
* **Database:** PostgreSQL
* **Mail Server:** SMTP credentials for email notifications

## Installation (Backend)

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-username/jwt-auth-app-backend.git
   ```
2. **Navigate to project root**

   ```bash
   cd jwt-auth-app-backend
   ```
3. **Build the project**

   ```bash
   mvn clean package
   ```

## Configuration (Backend)

Edit `src/main/resources/application.properties` with your environment-specific settings:

```properties
# Database settings
spring.datasource.url=jdbc:postgresql://<db_url>
spring.datasource.username=<db_username>
spring.datasource.password=<db_password>

# Mail server settings
spring.mail.host=<mail_host>
spring.mail.port=<mail_port>
spring.mail.username=<mail_username>
spring.mail.password=<mail_password>

# JWT configuration
jwt.secret.key=<secret_key>
jwt.token.access.expiration=<access_token_expiration>
jwt.token.refresh.expiration=<refresh_token_expiration>
```

## Running the Application (Backend)

By default, the backend runs at `http://localhost:8080`.

* **With Maven:**

  ```bash
  mvn spring-boot:run
  ```
* **From the JAR:**

  ```bash
  java -jar target/jwt-auth-app.jar
  ```

## Frontend

The React frontend is maintained in a separate repository: [varaiko/jwt-auth-app-frontend](https://github.com/varaiko/jwt-auth-app-frontend).

## API Endpoints

Here’s a revised, RESTful layout for your endpoints—using nouns (resources), pluralization, nesting, and query parameters for search:

### Authentication

| Method | URI                                       | Description                      |
| ------ | ----------------------------------------- | -------------------------------- |
| POST   | `/api/auth/register`                      | Register a new user              |
| POST   | `/api/auth/login`                         | Authenticate and receive a JWT   |
| POST   | `/api/auth/password/forgot`               | Request password reset email     |
| POST   | `/api/auth/password/reset`                | Reset password with reset token  |
| GET    | `/api/auth/password/verifyToken`          | Verify validity of a reset token |
| POST   | `/api/auth/token/refresh`                 | Refresh an expired JWT           |
| GET    | `/api/auth/token/verifyToken`             | Verify JWT token validity        |

### Roles & Permissions

| Method | URI                               | Description                             |
| ------ | --------------------------------- | --------------------------------------- |
| GET    | `/api/roles`                      | List all roles                          |
| GET    | `/api/roles/search`               | Search roles by name                    |
| POST   | `/api/roles`                      | Create a new role                       |
| GET    | `/api/roles/{roleId}`             | Retrieve role details                   |
| PUT    | `/api/roles/{roleId}/permissions` | Assign predefined permissions to a role |
| GET    | `/api/permissions`                | List all predefined permissions         |

### Stories

| Method | URI                                     | Description               |
| ------ | --------------------------------------- | ------------------------- |
| POST   | `/api/stories`                          | Create a new story        |
| GET    | `/api/stories`                          | Retrieve all stories      |
| GET    | `/api/stories/{storyId}`                | Retrieve a specific story |
| GET    | `/api/stories/search`                   | Search stories by keyword |
| PUT    | `/api/stories/{storyId}`                | Update a story            |
| DELETE | `/api/stories/{storyId}`                | Delete a story            |

### Comments

| Method | URI                                           | Description               |
| ------ | --------------------------------------------- | ------------------------- |
| POST   | `/api/stories/{storyId}/comments`             | Add a comment to a story  |
| GET    | `/api/stories/{storyId}/comments`             | List comments for a story |
| PUT    | `/api/stories/{storyId}/comments/{commentId}` | Update a specific comment |
| DELETE | `/api/stories/comments/{commentId}`           | Delete a specific comment |

### Users

| Method | URI                           | Description                      |
| ------ | ----------------------------- | -------------------------------- |
| GET    | `/api/users`                  | Retrieve all users               |
| GET    | `/api/users/search`           | Search users by name or email    |
| GET    | `/api/users/profile`          | Get authenticated user’s profile |
| GET    | `/api/users/{userId}`         | Get user info by ID              |
| PUT    | `/api/users/{userId}`         | Update user data                 |
| DELETE | `/api/users/{userId}`         | Delete a user                    |
