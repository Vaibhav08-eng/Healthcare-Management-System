# Online Healthcare Management System

A desktop-based healthcare management system built using Java Swing and MySQL. The application follows a clean MVC + DAO + Service architecture and supports three user roles: **Admin**, **Doctor**, and **Patient** — each with their own dashboards and workflows.

---

## 🔧 Tech Stack
- Java 8+
- Swing (desktop UI)
- MySQL 8.x
- JDBC (mysql-connector-j)
- MVC + DAO + Service multi-layer architecture
- SHA-256 password hashing

---

## 📌 Core Features

### 1. Authentication & Roles
- Secure login with SHA-256 password hashing
- Separate dashboards for Admin, Doctor, and Patient
- Role-based access control enforced at UI & service layers

### 2. Admin Portal
- Manage doctors and patients (add / update / remove)
- Manage system settings (key/value store)
- View appointment statistics & system analytics
- Full visibility into system operations

### 3. Doctor Portal
- Manage personal availability slots
- View upcoming appointments
- Access/update patient medical records
- View patient feedback & average ratings

### 4. Patient Portal
- Book, cancel, or reschedule appointments
- View personal medical history
- Submit ratings and feedback for doctors

### 5. Internal Logic & Validation
- Appointment conflict detection
- Input validation on all forms
- Centralized dialog & date utilities
- Extensible service layer for business rules

---

## 📁 Project Structure
```sql
healthcare-management-system/
│
├── lib/ # JDBC driver (mysql-connector-j)
│
├── sql/
│ └── healthcare_db.sql # Database schema + sample data
│
└── src/com/healthcare/
├── model/ # POJOs (User, Doctor, Appointment, etc.)
│
├── dao/ # DAO interfaces
│ └── impl/ # JDBC implementations
│
├── service/ # Business logic classes
│
├── ui/ # Swing forms and dashboards
│ ├── admin/
│ ├── doctor/
│ └── patient/
│
└── util/ # PasswordUtil, DateUtil, UiUtil, etc.

---
```
## 🗄 Database Setup

1. Install **MySQL 8.x**
2. Open MySQL Workbench
3. Run:

SOURCE sql/healthcare_db.sql;
This creates:
->The database & tables
->Seeded data (Admin, Doctor, Patient accounts)

Default Login
| Role  | Email                                       | Password |
| ----- | ------------------------------------------- | -------- |
| Admin | [admin@health.com](mailto:admin@health.com) | admin123 |

▶️ Running the Application
1.Open the project in IntelliJ IDEA / Eclipse
2.Add the MySQL connector:
->Place mysql-connector-j.jar inside lib/
->Add it to your project/module dependencies
3. Update DB credentials in:
src/com/healthcare/dao/ConnectionFactory.java
4.Build and run:
com.healthcare.ui.LoginFrame
5.Log in using the seeded accounts

🧱 Architecture Overview
Model Layer
Contains basic data objects used by DAOs and services.

DAO Layer
Responsible for all database interactions using JDBC.
No SQL logic is mixed into UI components.

Service Layer
Handles business rules:
->Appointment slot conflict checks
->User validation
->Role-specific operations

UI Layer (Swing)
Screens invoke services — never DAOs directly.
Separation ensures cleaner, maintainable code.

📊 Analytics
The system includes a lightweight analytics module (ReportService), providing:
->Total users
->Doctor count
->Appointment summaries
->Basic trends
No external analytics libraries required.

🚀 Why This Project Matters
->Uses real industry patterns (MVC, DAO, service abstraction)
->Clean separation of concerns
->Expandable dashboards for three different user types
->Strong foundation for academic, portfolio, or internship submissions

📜 License
MIT License — free for learning, modification, and personal use.
-->The project is open for educational purposes and personal use . Attribution optional appreciated. 
