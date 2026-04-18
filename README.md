<div align="center">

# 💸 Expense Control API

**Track your expenses with security, organization, and elegance.**

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

</div>

---

## 📖 About

**Expense Control API** is a RESTful API built with **Java 21** and **Spring Boot 3.5**, designed for managing personal or business expenses. It features **JWT-based authentication**, auto-generated **Swagger/OpenAPI** documentation, database versioning with **Flyway**, and is fully containerized with **Docker**.

---

## 🚀 Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core language |
| Spring Boot | 3.5.0 | Base framework |
| Spring Security | — | Authentication & authorization |
| Spring Data JPA | — | Data persistence |
| PostgreSQL | — | Relational database |
| Flyway | — | Database migration & versioning |
| JWT (JJWT) | 0.12.5 | Authentication tokens |
| MapStruct | 1.6.0 | DTO mapping |
| Lombok | — | Boilerplate reduction |
| SpringDoc OpenAPI | 2.8.5 | Swagger documentation |
| OpenCSV | 5.9 | CSV parsing & conversion |
| Docker | — | Containerization |

---

## 🏗️ Architecture

The project follows a clean layered architecture:

```
src/
└── main/
    └── java/com/brenno/expensecontrol/
        ├── controller/    # Presentation layer (REST endpoints)
        ├── service/       # Business logic
        ├── repository/    # Data access layer (JPA)
        ├── domain/        # Entities and domain models
        ├── dto/           # Data Transfer Objects
        ├── mapper/        # Entity ↔ DTO conversion (MapStruct)
        ├── security/      # JWT & Spring Security configuration
        └── config/        # General application configuration
```

---

## ⚙️ Prerequisites

Make sure you have the following installed:

- [Java 21+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/)
- [Docker & Docker Compose](https://www.docker.com/)
- [PostgreSQL](https://www.postgresql.org/) *(or just use Docker Compose below)*

---

## 🐳 Running with Docker

The project uses Docker Compose **profiles** to control what gets started depending on the environment:

- **Development** — starts the database only; the API runs locally via Maven
- **Production** — starts both the API and the database together

> See the [Production Deployment](#-production-deployment) section for full details on each profile.

### Development (database only)

```bash
# Clone the repository
git clone https://github.com/BrennoGuimaraes/expense-control.git
cd expense-control

# Start the database
docker-compose up -d

# Run the API locally
./mvnw spring-boot:run
```

### Production (API + database)

```bash
docker-compose --profile prod up -d --build
```

The API will be available at: **`http://localhost:8080`**

---

## 🔐 Authentication

The API uses **JWT (JSON Web Token)** for authentication. The basic flow is:

1. **Register** a user via `POST /auth/register`
2. **Login** via `POST /auth/login` — the response returns a JWT token
3. Include the token in the header of all protected requests:

```http
Authorization: Bearer <your-token-here>
```

> Tokens expire after the configured duration. Renew via the refresh endpoint if available.

---

## 📚 API Documentation (Swagger)

With the application running, access the interactive docs at:

```
http://localhost:8080/swagger-ui.html
```

You can explore all available endpoints, their parameters, request/response schemas, and even test calls directly from your browser.

---

## 🗄️ Database Migrations (Flyway)

The database schema is managed automatically by **Flyway**. Migration files are located at:

```
src/main/resources/db/migration/
```

Migrations are applied automatically on startup, keeping your database schema always in sync — no surprises.

---

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage report
./mvnw verify
```

The project uses **Spring Boot Test** and **Spring Security Test** to ensure feature quality and reliability.

---

## 📦 Production Deployment

The project uses **Docker Compose profiles** to control what gets started in each environment — no extra files or overrides needed.

### 🔁 Environment Overview

| Profile | Command | What starts |
|---|---|---|
| **Development** | `docker-compose up -d` | Database only — run the API locally with Maven |
| **Production** | `docker-compose --profile prod up -d --build` | API + Database together |

### 🚀 Running in Production

```bash
# Clone the repository
git clone https://github.com/BrennoGuimaraes/expense-control.git
cd expense-control

# Start the API + database with the production profile
docker-compose --profile prod up -d --build
```

> The `--build` flag ensures the latest image is built before starting the containers.

The API will be available at: **`http://localhost:8080`**

### 🔧 Running in Development

```bash
# Starts only the database — run the API separately via Maven
docker-compose up -d

./mvnw spring-boot:run
```

### 🔑 Environment Variables

Before deploying to production, configure the following environment variables (via `.env` file or your hosting provider's secrets manager):

```env
# Database
POSTGRES_DB=expensecontrol
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_secure_password

# Application
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/expensecontrol
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_very_long_and_secure_secret_key
JWT_EXPIRATION=86400000
```

> ⚠️ Never commit your `.env` file to version control. Add it to `.gitignore`.

### 🛑 Stopping the containers

```bash
docker-compose down

# To also remove volumes (database data)
docker-compose down -v
```


## 👨‍💻 Author

Crafted with ☕ by **Brenno Guimarães**

[![GitHub](https://img.shields.io/badge/GitHub-BrennoGuimaraes-181717?style=flat&logo=github)](https://github.com/BrennoGuimaraes)
