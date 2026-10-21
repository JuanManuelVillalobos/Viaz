# Viaz — Logistics Operations API

![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![AWS S3](https://img.shields.io/badge/AWS-S3-yellow?style=flat-square&logo=amazonaws)
![License](https://img.shields.io/badge/License-All_Rights_Reserved-red?style=flat-square)

> **Portfolio Project:** Production-grade RESTful backend for a logistics operations dashboard designed to digitize Excel-based fulfillment workflows, track multi-leg cross-border shipments, and manage dynamic role-based access control (RBAC).

---

## Overview

The application centralizes core logistics operations across distinct domain modules:

- **Shipments:** Lifecycle state tracking, multi-leg duplication (e.g., US/MX legs), and incident locking.
- **Access Control:** Dynamic permission enforcement, user role mapping, and secure token issuance.
- **Analytics:** Operational metrics, rating systems for carriers/clients, and performance reporting.
- **File & Communication:** S3 document uploads, automated email notifications, and Excel export/import.

---

## Key Technical Highlights

* **Multi-Leg Shipment Versioning:** Custom duplication logic supporting alphabetic variant tracking (`A`, `B`, `C`...) for multi-leg international freight routes.
* **Dual-Token Authentication & Custom RBAC:** Dual-token JWT security model (15-min access token + httpOnly refresh token) paired with custom Spring Security filters for fine-grained UI/API permission checks.
* **Database Migrations:** Schema changes fully version-controlled using **Flyway** with automatic startup bootstrapping.
* **Resilient Infrastructure:** Configured with rate-limiting filters, Caffeine memory caching, and Elastic Beanstalk / Docker Jib deployment profiles.

---

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.x, Spring Data JPA, Spring Security, Spring Web
* **Database:** PostgreSQL, Flyway
* **Security:** JWT (`jjwt`), BCrypt, Custom Rate-Limiting Filters
* **Cloud & Tools:** AWS S3 SDK, Apache POI (Excel), Caffeine Cache, Lombok, Maven

---

## Project Structure

```text
.
├── .github/ workflows & CI/CD
├── elasticbeanstalk/ deployment configurations
├── src/
│   ├── main/
│   │   ├── java/com/viaz/dashboard/
│   │   │   ├── auth/          # Authentication & token endpoints
│   │   │   ├── config/        # Spring Boot & security configs
│   │   │   ├── email/         # SMTP integration
│   │   │   ├── exception/     # Global error handling
│   │   │   ├── features/      # Domain-driven feature modules
│   │   │   │   ├── carrier/
│   │   │   │   ├── charge/
│   │   │   │   ├── client/
│   │   │   │   ├── incident/
│   │   │   │   ├── notification/
│   │   │   │   ├── permission/
│   │   │   │   ├── rating/
│   │   │   │   ├── role/
│   │   │   │   ├── shipment/
│   │   │   │   └── user/
│   │   │   ├── filters/       # Rate limiting & JWT security filters
│   │   │   ├── initializers/  # Admin bootstrap
│   │   │   ├── s3/            # AWS S3 file management
│   │   │   └── stats/         # Analytics & KPI aggregation
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/  # Flyway schema scripts
├── pom.xml
└── README.md
```

---

## Configuration

Main configuration file: `src/main/resources/application.yaml`

Environment variables required for runtime:

```bash
# Authentication & Security
JWT_SECRET=your_jwt_secret_key
DEFAULT_ADMIN_USER=admin
DEFAULT_ADMIN_PASSWORD=your_secure_password
RATE_LIMIT=100

# Application & AWS
FRONTEND_URL=http://localhost:3000
VIAZ_ID_START_NUMBER=1000
AWS_REGION=us-east-1
MAIL_PASSWORD=your_mail_password

# Database Settings
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/viaz
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password
```

---

## Local Setup

### Requirements
* Java 21+
* Maven 3.9+
* PostgreSQL 15+

### Installation

1. **Clone repository:**
   ```bash
   git clone [https://github.com/JuanManuelVillalobos/Viaz.git](https://github.com/JuanManuelVillalobos/Viaz.git)
   cd Viaz
   ```

2. **Build application:**
   ```bash
   mvn clean install
   ```

3. **Run application:**
   ```bash
   mvn spring-boot:run
   ```
   The REST API will be available at `http://localhost:8080`.

---

## License

Copyright © 2025–2026 Juan Manuel Villalobos. All Rights Reserved.

This repository is published strictly for portfolio display and code review. Unlawful copying, distribution, or commercial use of this software is strictly prohibited.
