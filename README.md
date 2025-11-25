Online Healthcare Management System

A Java Swing + MySQL desktop application designed to streamline key operations in a healthcare facility.
The system supports three user roles — Admin, Doctor, and Patient — each with dedicated dashboards, workflows, and permissions.

The project follows a clean MVC + DAO + Service architecture for maintainability and scalability.

🔧 Tech Stack

Java 8+

Swing (desktop UI)

MySQL 8.x

JDBC (mysql-connector-j)

MVC + DAO + Service layering

SHA-256 password hashing

📌 Core Features
1. Role-Based Authentication

Login screens for Admin, Doctor, and Patient

Access control enforced at UI and service layers

Passwords stored as secure SHA-256 hashes

2. Admin Module

Manage users: create/update/delete doctors & patients

Configure system settings (key/value store)

View basic analytics: doctor count, appointments, patient stats

Access appointment overview and global system metrics

3. Doctor Module

Manage availability schedule

View upcoming appointments

Update patient records & medical history

Check patient feedback and average ratings

4. Patient Module

Book, cancel, or reschedule appointments

View complete medical history

Provide feedback & rating for doctors

5. Additional Utilities

Appointment conflict detection

Input validation and UI alerts

Reusable helpers for hashing, date formatting, and dialogs

Modular architecture for easy expansion

📁 Project Structure
healthcare-management-system/
│
├── lib/                     # JDBC driver (mysql-connector-j)
│
├── sql/
│   └── healthcare_db.sql    # Database schema + seed data
│
└── src/com/healthcare/
    ├── model/               # POJOs (User, Doctor, Appointment, Records, etc.)
    │
    ├── dao/                 # DAO interfaces
    │   └── impl/            # JDBC implementations
    │
    ├── service/             # Business logic (AdminService, DoctorService, etc.)
    │
    ├── ui/                  # Swing GUI screens
    │   ├── admin/           # Admin dashboard panels
    │   ├── doctor/          # Doctor dashboard panels
    │   └── patient/         # Patient dashboard panels
    │
    └── util/                # Helpers (PasswordUtil, UiUtil, DateUtil, etc.)

🗄 Database Setup

Install MySQL 8.x

Open MySQL Workbench

Run:

SOURCE sql/healthcare_db.sql;


This will:

Create the database

Build all tables

Insert sample users

Default Credentials
Role	Email	Password
Admin	admin@health.com
	admin123
▶️ Running the Application

Open the project in IntelliJ IDEA or Eclipse

Add the MySQL connector:

Place mysql-connector-j.jar inside lib/

Add it to your module dependencies

Configure database credentials in:

src/com/healthcare/dao/ConnectionFactory.java


Build the project

Run:

com.healthcare.ui.LoginFrame


Login using seeded accounts

🧱 Architecture Overview
Model Layer

Defines plain data objects used across the application.

DAO Layer

Handles all database operations using JDBC.
UI never touches SQL directly.

Service Layer

Applies business rules:

Slot clash detection

Profile data validation

Role-specific operations

UI Layer

Java Swing forms and panels that call service methods.
Clean separation ensures easy maintenance.

📊 Analytics

A lightweight analytics module (ReportService) provides:

Total users

Total doctors

Appointment stats

Slot utilization trends

No external libraries required.

🚀 Why This Project Stands Out

Entirely structured around real-world enterprise patterns

Clean separations: UI ↔ Service ↔ DAO

Fully navigable multi-role dashboards

Beginner-friendly to understand, yet industry-graded in architecture

Suitable for academic submissions, internships, or portfolio projects

📜 License

This project is open for educational and personal use.
Attribution optional but appreciated.

🤝 Contributions

Issues, improvements, and feature requests are welcome.
