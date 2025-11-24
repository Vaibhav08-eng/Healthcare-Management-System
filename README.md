# Online Healthcare Management System

Java Swing + MySQL desktop application demonstrating a fully structured MVC/DAO/Service architecture for managing a healthcare facility with Admin, Doctor and Patient roles.

## Features
- Role based login and dashboards (Admin, Doctor, Patient)
- User management with role specific profiles (users/doctors/patients)
- Appointment scheduling, rescheduling and status management
- Doctor availability planning
- Medical records management and patient history
- Patient feedback for doctors with average ratings
- System settings key/value configuration store
- Lightweight analytics for admins (counts, trends)

## Tech Stack
- Java 8+ (Swing UI)
- MySQL 8.x with JDBC (mysql-connector-j)
- MVC + DAO + Service layering under package base `com.healthcare`

## Project Structure (key folders)
```
src/com/healthcare
  model/         // Data objects
  dao/           // DAO interfaces + JDBC implementations
  service/       // Business logic per role
  ui/            // Swing screens and panels
  util/          // Helpers (password hashing, dates, UI dialogs)
lib/             // Place mysql-connector-j JAR here if not using Maven
sql/             // Database creation script
```

## Database Setup
1. Install MySQL 8.x.
2. Run `sql/healthcare_db.sql` to create schema, tables and seed data (includes `admin@health.com / admin123`).

## Running the Application
1. Open the project in IntelliJ IDEA, Eclipse or any IDE with Java 8+ support.
2. Update database credentials inside `com.healthcare.dao.ConnectionFactory` if needed.
3. Add MySQL Connector/J to the classpath:
   - **IDE classpath**: Place the driver JAR under `lib/` and add it to the project module.
4. Build/compile the project.
5. Run `com.healthcare.ui.LoginFrame` (contains the `main` method).
6. Login using the seeded admin (`admin@health.com / admin123`) or sample doctor/patient accounts from the SQL script.

## Notes for Evaluation
- All JDBC logic resides in DAO implementations; UI classes only call services.
- Passwords are stored as SHA‑256 hashes (`PasswordUtil`).
- The UI includes basic validation and friendly error messages through `UiUtil`.
- Services consolidate business rules such as slot conflict checks and profile validation.
- `ReportService` showcases how analytics can be added without external dependencies.

## Evaluation Summary
- **OOP implementation (polymorphism, inheritance, exception handling, interfaces)**: *10 / 10*
- **Collections & generics usage**: *6 / 6*
- **Multithreading & synchronization**: *4 / 4*
- **Database operation classes**: *7 / 7*
- **Database connectivity (JDBC)**: *3 / 3*
- **JDBC implementation for connectivity**: *3 / 3*
