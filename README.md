# Viaz — Logistics Operations API

![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![AWS S3](https://img.shields.io/badge/AWS-S3-yellow?style=flat-square&logo=amazonaws)
![License](https://img.shields.io/badge/License-All_Rights_Reserved-red?style=flat-square)

> **Portfolio Project:** Production-grade RESTful backend for a logistics management MVP built to replace a 5-person team's Excel-based workflow. Designed to centralize cross-border multi-leg shipment tracking, incident reporting with rating statistics, AWS S3 document management, and dynamic permission-based RBAC.

---

## Overview

The application centralizes core logistics operations across specialized domain modules:

- **Shipments & Variant Tracking:** Multi-field loose search, status/client/carrier filtering, and cross-border shipment duplication with alphabetic versioning (`A`, `B`, `C`...) for multi-leg routes (e.g., US leg + MX leg).
- **Incident & Rating Module:** Incident and charge tracking logged against clients or carriers, backed by weighted rating algorithms with historical statistics and state locking upon shipment closure (edits restricted unless reopened via permission).
- **Authentication & RBAC:** Fine-grained Role-Based Access Control (RBAC) with custom roles, multi-role assignment, cosmetic dropdown visibility rules, dual-token JWT security, and automated password resets.
- **Analytics & Documents:** Operational KPIs, monthly rating summaries, S3 document storage, automated email notifications, and Excel import/export data processing.

---

## Key Technical Highlights

* **Multi-Leg Cross-Border Duplication:** Custom business logic allowing operational teams to clone and version shipments with alphabetic suffixes (`A`, `B`, `C`...) to track distinct international transit legs (e.g., border crossing vs. domestic delivery).
* **Dual-Token Authentication & Dynamic RBAC:** Secure token system using **15-minute JWT access tokens** and **30-day httpOnly refresh tokens**, BCrypt hashing, custom Spring Security/rate-limiting filters, and **15-minute expiry token-based password reset** flows. Enforces custom role permissions and cosmetic UI visibility rules on backend endpoints.
* **Incident Lifecycle & Permission Locking:** Incident reporting system tied to client/transport rating statistics. Closing a shipment automatically locks edits on the backend unless explicit override permissions are granted.
* **Excel Workflow Digitization:** Integrated **Apache POI** for Excel import/export alongside **Flyway** schema migrations, **Caffeine** in-memory caching, and **AWS S3** document attachments.

---

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.5.5, Spring Data JPA, Spring Security, Spring Web
* **Database & Migrations:** PostgreSQL, Flyway
* **Security & Auth:** JWT (`jjwt`), BCrypt, Custom Rate-Limiting & JWT Security Filters
* **Cloud & Tools:** AWS SDK for S3, Elastic Beanstalk, Docker Jib, Apache POI (Excel), Caffeine Cache, Lombok, Maven

---

## Project Structure

```text
.
├── .github/ workflows & CI/CD
├── elasticbeanstalk/ deployment configurations
├── src/
│   ├── main/
│   │   ├── java/com/viaz/dashboard/
│   │   │   ├── auth/          # Authentication & password reset
│   │   │   ├── config/        # Spring Boot & security configurations
│   │   │   ├── email/         # SMTP email notifications
│   │   │   ├── exception/     # Global error handling
│   │   │   ├── features/      # Domain-driven feature modules
│   │   │   │   ├── carrier/   # Carrier management & rating
│   │   │   │   ├── charge/    # Shipment charges
│   │   │   │   ├── client/    # Client records & ratings
│   │   │   │   ├── incident/  # Incident tracking & logs
│   │   │   │   ├── notification/ # System notifications
│   │   │   │   ├── permission/ # Dynamic permission definitions
│   │   │   │   ├── rating/    # Weighted rating algorithms
│   │   │   │   ├── role/      # Custom multi-role assignments
│   │   │   │   ├── shipment/  # Shipment lifecycle & multi-leg versioning
│   │   │   │   └── user/      # User management
│   │   │   ├── filters/       # JWT validation & rate-limiting filters
│   │   │   ├── initializers/  # Admin bootstrap initialization
│   │   │   ├── jwt/           # Token generation & verification
│   │   │   ├── s3/            # AWS S3 file upload & download
│   │   │   ├── scheduled/     # Scheduled background tasks
│   │   │   ├── security/      # Security context & RBAC logic
│   │   │   └── stats/         # Analytics, KPI aggregation & reporting
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

# Application & AWS Settings
FRONTEND_URL=http://localhost:3000
VIAZ_ID_START_NUMBER=1000
AWS_REGION=us-east-1
MAIL_PASSWORD=your_mail_password

# Weighted Rating Coefficients
CARRIER_RATING_WEIGHT=0.25
CLIENT_RATING_WEIGHT=0.25
CARRIER_CUSTOMER_SERVICE_RATING_WEIGHT=0.25
CLIENT_CUSTOMER_SERVICE_RATING_WEIGHT=0.25

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
   The REST API will be served at `http://localhost:8080`.

---

## License

Copyright © 2025–2026 Juan Manuel Villalobos. All Rights Reserved.

This repository is published strictly for portfolio display and technical code review. Unlawful copying, distribution, modification, or commercial deployment of this software is strictly prohibited.
