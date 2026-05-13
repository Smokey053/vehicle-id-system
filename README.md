# Plate IQ — Vehicle Identification System

A JavaFX desktop application for managing vehicle registrations, service records, insurance policies, police reports, and customer queries, backed by a PostgreSQL database.

---

## Features

| Role | Capabilities |
|---|---|
| **Admin** | Full access to all modules; user management |
| **Workshop** | Manage vehicles and service records |
| **Insurance** | Manage insurance policies and view vehicles |
| **Police** | File police reports, log violations, view vehicles |
| **Customer** | View own vehicle info and submit queries |

- Vehicle registration with engine/chassis number tracking
- Service history with odometer readings and invoicing
- Insurance policy and claims management
- Police report and traffic violation logging
- Role-based access control enforced throughout the UI
- Text-based report/data export

---

## Tech Stack

- **Java 17**
- **JavaFX 22** — UI framework (FXML + CSS)
- **PostgreSQL** — relational database
- **Maven** — build and dependency management

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 17 |
| Apache Maven | 3.6+ |
| PostgreSQL | 13+ |

---

## Setup

**1. Create the database**

Run `schema.sql` against your PostgreSQL instance:

```vehicle-id-system/schema.sql#L1-5
-- Database: plateiq-db
CREATE DATABASE "plateiq-db" ...
```

```/dev/null/shell.sh#L1-1
psql -U postgres -f schema.sql
```

**2. Configure the database connection**

Update the connection settings in `DBConnection.java` (host, port, username, password) to match your PostgreSQL instance.

**3. Run the application**

On Windows, use the provided script:

```vehicle-id-system/run-app.bat#L1-3
@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
...
```

Or run directly with Maven:

```/dev/null/shell.sh#L1-1
mvn clean compile exec:java@run-app
```

---

## Project Structure

```/dev/null/tree.txt#L1-10
src/main/java/com/plateiq/
├── controller/   # JavaFX controllers (Login, Dashboard, Vehicle, Service, Insurance, Police, Customer)
├── database/     # DBConnection singleton
├── model/        # Domain models (User, Vehicle, Customer, ServiceRecord, InsurancePolicy, PoliceReport, ...)
├── service/      # Business logic / data access layer
└── utils/        # AccessControl, SessionManager, SceneNavigator, ReportExporter, AlertUtils
```

---

## License

This project is for educational/internal use. No license is currently specified.
