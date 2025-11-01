# JWT Authentication Backend with MySQL

A Spring Boot application implementing JWT-based authentication with MySQL database, following best practices including DTOs, Mappers, proper layering, and exception handling.

## 🏗️ Architecture & Best Practices

### Project Structure

```
src/main/java/ma/enset/jwtauthlab/
├── config/              # Configuration classes
│   ├── ApplicationConfig.java
│   └── SecurityConfig.java
├── controller/          # REST controllers
│   └── AuthController.java
├── dto/                 # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   └── UserResponse.java
├── entity/             # JPA entities
│   └── User.java
├── exception/          # Custom exceptions and handlers
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── UserAlreadyExistsException.java
├── filter/             # Security filters
│   └── JwtAuthFilter.java
├── mapper/             # Entity-DTO mappers
│   └── UserMapper.java
├── repository/         # JPA repositories
│   └── UserRepository.java
└── service/            # Business logic
    ├── JwtService.java
    ├── MyUserDetailsService.java
    └── UserService.java
```

### Key Features

- ✅ **MySQL Integration** with Spring Data JPA
- ✅ **JWT Authentication** with secure token generation
- ✅ **User Registration** with validation
- ✅ **DTO Pattern** for separation of concerns
- ✅ **Mapper Pattern** for entity-DTO conversion
- ✅ **Global Exception Handling** with custom error responses
- ✅ **Bean Validation** on request DTOs
- ✅ **Repository Pattern** with custom queries
- ✅ **Service Layer** for business logic
- ✅ **Password Encryption** with BCrypt
- ✅ **Audit Fields** (createdAt, updatedAt) with Hibernate annotations

## 🚀 Setup Instructions

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Your favorite IDE (IntelliJ IDEA, VS Code, etc.)

### 1. Database Setup

Start MySQL and create the database (or let Spring Boot auto-create it):

```bash
# Connect to MySQL
mysql -u root -p

# Create database (optional - Spring Boot can auto-create)
CREATE DATABASE jwt_auth_db;
```

### 2. Configure Database

Edit `src/main/resources/application.properties` if needed:

```properties
# Update these values based on your MySQL setup
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_auth_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📋 API Endpoints

### 1. Register a New User

**POST** `/api/auth/register`

**Request Body:**

```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "roles": ["ROLE_USER"]
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"],
  "enabled": true,
  "createdAt": "2025-11-01T10:30:00",
  "updatedAt": "2025-11-01T10:30:00"
}
```

### 2. Login

**POST** `/api/auth/login`

**Request Body:**

```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com"
}
```

### 3. Access Protected Endpoint

**GET** `/api/hello`

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Response (200 OK):**

```json
{
  "message": "Bonjour, endpoint protégé OK ✅"
}
```

## 🧪 Testing with cURL

### Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "roles": ["ROLE_USER"]
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Access Protected Endpoint

```bash
# Replace <JWT_TOKEN> with the token from login response
curl -X GET http://localhost:8080/api/hello \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 🔒 Security Configuration

- **Public Endpoints:** `/api/auth/login`, `/api/auth/register`
- **Protected Endpoints:** All other `/api/**` endpoints
- **Password Encryption:** BCrypt with strength 10
- **JWT Expiration:** 1 hour (configurable in `application.properties`)

## ⚙️ Configuration

Key configuration properties in `application.properties`:

```properties
# JWT Settings
app.jwt.secret=your-secret-key
app.jwt.expiration-ms=3600000  # 1 hour
app.jwt.issuer=jwt-auth-lab

# Database Settings
spring.jpa.hibernate.ddl-auto=update  # Creates/updates tables automatically
spring.jpa.show-sql=true              # Shows SQL queries in console
```

## 🗃️ Database Schema

The application auto-creates the following tables:

### `users` table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### `user_roles` table

```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 🛠️ Technologies Used

- **Spring Boot 3.5.7**
- **Spring Security 6**
- **Spring Data JPA**
- **MySQL 8**
- **JWT (jjwt 0.11.5)**
- **Lombok**
- **Bean Validation (Jakarta)**
- **Maven**

## 📝 Error Handling

The application includes comprehensive error handling:

- **400 Bad Request:** Validation errors
- **401 Unauthorized:** Invalid credentials
- **404 Not Found:** User not found
- **409 Conflict:** Username/email already exists
- **500 Internal Server Error:** Unexpected errors

Example error response:

```json
{
  "timestamp": "2025-11-01T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username is already taken: john_doe",
  "path": "/api/auth/register"
}
```

## 🔄 Migration from In-Memory to MySQL

This project has been refactored from in-memory authentication to MySQL-backed authentication with the following improvements:

1. **Persistent Storage:** Users are now stored in MySQL database
2. **Registration Endpoint:** New users can self-register
3. **DTOs:** Request/Response separation from entities
4. **Mappers:** Clean conversion between layers
5. **Validation:** Input validation with Jakarta Bean Validation
6. **Exception Handling:** Global exception handler for consistent error responses
7. **Service Layer:** Business logic separated from controllers
8. **Repository Pattern:** Custom queries for flexible data access

## 📚 Next Steps

To further enhance this application, consider:

- Add refresh token functionality
- Implement password reset via email
- Add role-based access control on endpoints
- Create admin endpoints for user management
- Add integration tests
- Implement rate limiting
- Add API documentation with Swagger/OpenAPI
- Configure CORS for frontend integration

## 📄 License

This project is for educational purposes.
