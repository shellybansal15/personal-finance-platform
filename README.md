# Online Personal Finance Management Platform (FinanceFlow)

> A modern, full-stack, enterprise-grade personal finance management web application built with **Java 17**, **Jakarta Servlets & JSP**, **Tomcat 10.1**, and **MySQL 8** with pure JDBC. Designed and structured for college B.Tech Computer Science curriculum grading criteria.

---

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Grading Rubric Implementation Guide](#grading-rubric-implementation-guide)
4. [Fixed Tech Stack](#fixed-tech-stack)
5. [Project Folder Structure](#project-folder-structure)
6. [Database Setup Instructions](#database-setup-instructions)
7. [How to Build & Run on Apache Tomcat 10.1](#how-to-build--run-on-apache-tomcat-101)
8. [Default Login Credentials](#default-login-credentials)
9. [Design System & Themes](#design-system--themes)
10. [UI Screenshots](#ui-screenshots)

---

## 🌟 Project Overview

**FinanceFlow** provides individuals and financial administrators with an end-to-end platform to monitor day-to-day expenditures, enforce category budgets, track savings goals, and audit administrative actions. The application features a clean fintech aesthetic—**white and emerald green in light mode** and **deep black and emerald green in dark mode**—with interactive Chart.js visualizations, responsive mobile navigation, and bank-grade data security.

---

## 🚀 Key Features

### For Regular Users:
- **Financial Dashboard**: 4 key statistical metric cards, Chart.js doughnut chart of expenses by category, 6-month spending trend line chart, and recent activity.
- **Expense Logging**: Record expenses with amounts, categories, dates, and private notes.
- **Financial Data Security**: Sensitive expense notes are encrypted at rest using **AES-256**.
- **Asynchronous CSV Export**: Submits CSV export generation jobs to a background **ExecutorService** thread pool.
- **Budget Tracking (`Trackable`)**: Establish category limits with dynamic progress bars that turn **amber at 80%** and **red at 100%**.
- **Savings Goals (`Trackable`)**: Set targets and deadlines with progress bars and an interactive **Contribute Funds** modal.
- **Floating Action Button (FAB)**: Quick 1-click expense creation modal accessible from any screen.

### For Administrators:
- **Admin Command Center**: Platform overview metrics, active user distribution, and recent security logs.
- **User Directory**: Searchable, paginated user management table with account status toggles (`ACTIVE` / `BLOCKED`) and role reassignment (`USER` / `ADMIN`).
- **Administrative Self-Preservation**: Built-in safeguards preventing administrators from blocking or demoting their own accounts.
- **Security Audit Logs**: Chronological review of all authentication, lockout, and profile modification events.
- **System Settings**: Live configuration toggles (`allow_registration`, `max_login_attempts`, `session_timeout_minutes`).

---

## 🎓 Grading Rubric Implementation Guide

| Syllabus Rubric Requirement | Where It Is Visible in the Codebase |
| :--- | :--- |
| **OOP: Inheritance & Polymorphism** | Abstract [`Transaction.java`](src/main/java/com/finance/model/Transaction.java) extended by concrete [`Expense.java`](src/main/java/com/finance/model/Expense.java) and [`Income.java`](src/main/java/com/finance/model/Income.java) with polymorphic `getType()` overrides. |
| **OOP: Interfaces** | [`Trackable.java`](src/main/java/com/finance/model/Trackable.java) interface implemented by [`Budget.java`](src/main/java/com/finance/model/Budget.java) and [`Goal.java`](src/main/java/com/finance/model/Goal.java) with default methods for progress evaluation. |
| **OOP: Custom Exception Hierarchy** | [`AppException.java`](src/main/java/com/finance/exception/AppException.java) base class with domain subclasses: `AuthenticationException`, `ValidationException`, `DatabaseException`, `ResourceNotFoundException`, and `UnauthorizedException`. |
| **Collections & Generics** | Generic [`BaseDAO<T>.java`](src/main/java/com/finance/dao/BaseDAO.java) interface; `List<T>` and `Map<K, V>` used in [`ExpenseDAO.java`](src/main/java/com/finance/dao/ExpenseDAO.java) for Chart.js aggregations. |
| **Multithreading: Background Workers** | [`ReportService.java`](src/main/java/com/finance/service/ReportService.java) executes CSV generation jobs asynchronously using an `ExecutorService` thread pool. |
| **Multithreading: Synchronization** | [`AuditLogger.java`](src/main/java/com/finance/util/AuditLogger.java) uses `synchronized` methods to prevent race conditions and ensure thread-safe logging. |
| **Multithreading: Concurrent Collections** | [`LoginThrottler.java`](src/main/java/com/finance/util/LoginThrottler.java) uses `ConcurrentHashMap` to track failed login attempts and enforce 15-minute lockouts after 5 consecutive failures. |
| **DAO Separation & PreparedStatement** | Independent DAO classes for every entity (`UserDAO`, `ExpenseDAO`, `BudgetDAO`, `GoalDAO`, `CategoryDAO`, `SecurityLogDAO`, `SystemSettingDAO`). |
| **Pure JDBC & try-with-resources** | [`DBConnection.java`](src/main/java/com/finance/util/DBConnection.java) and every DAO query strictly uses `PreparedStatement` with `try-with-resources`. |
| **MVC & Post-Redirect-Get (PRG)** | `@WebServlet` controllers dispatching to JSP views; form submissions execute PRG and display confirmation flash messages on every action. |
| **Web Security Controls** | BCrypt password hashing via [`PasswordUtil.java`](src/main/java/com/finance/util/PasswordUtil.java), AES-256 note encryption via [`EncryptionUtil.java`](src/main/java/com/finance/util/EncryptionUtil.java), CSRF tokens validated on all POST requests via [`CsrfFilter.java`](src/main/java/com/finance/filter/CsrfFilter.java), and 15-minute session timeouts. |

---

## 🛠️ Fixed Tech Stack

- **Backend**: Java 17, Jakarta Servlet 6.0 (`jakarta.servlet`), Jakarta Server Pages (JSP) 3.1, JSTL 3.0
- **Build Tool**: Apache Maven 3.9+
- **Application Server**: Apache Tomcat 10.1+ (Jakarta EE namespace)
- **Database**: MySQL 8.0+ with Plain JDBC (`mysql-connector-j` 8.3.0)
- **Cryptography**: jBCrypt 0.4 (Passwords) & AES-256 (Expense Notes)
- **Frontend**: Bootstrap 5.3.3, Bootstrap Icons 1.11, Chart.js 4.4, Vanilla JavaScript (ES6)

---

## 📁 Project Folder Structure

```
personal-finance-platform/
├── pom.xml                                     # Maven build file (configured for Java 17 & Tomcat 10.1)
├── .gitignore                                  # Git ignore rules
├── README.md                                   # Comprehensive project documentation
├── docs/
│   ├── ARCHITECTURE.md                         # Mermaid layered MVC diagram
│   └── DB_DIAGRAM.md                           # Mermaid database ER diagram
├── sql/
│   ├── schema.sql                              # DDL schema (7 tables, foreign keys, CHECK constraints, indexes)
│   └── seed.sql                                # Sample seed data (1 admin, 1 user, 8 categories, 15 expenses)
├── src/
│   └── main/
│       ├── java/com/finance/
│       │   ├── dao/                            # Data Access Layer (BaseDAO<T>, UserDAO, ExpenseDAO...)
│       │   ├── exception/                      # AppException hierarchy
│       │   ├── filter/                         # AuthFilter, AdminFilter, CsrfFilter
│       │   ├── model/                          # Entities (Transaction -> Expense, Budget/Goal -> Trackable)
│       │   ├── service/                        # Business logic, ReportService (ExecutorService)
│       │   ├── servlet/                        # Controllers (@WebServlet, Post-Redirect-Get)
│       │   └── util/                           # DBConnection, PasswordUtil, LoginThrottler, AuditLogger...
│       ├── resources/
│       │   ├── db.properties.example           # Configuration template
│       │   └── db.properties                   # Local database credentials (gitignored)
│       └── webapp/
│           ├── WEB-INF/
│           │   ├── web.xml                     # Deployment descriptor (15-min timeout, error pages)
│           │   └── jsp/                        # Modular JSP views (fragments, auth, user, admin, error)
│           ├── css/main.css                    # Design system (Light: White & Green, Dark: Black & Green)
│           └── js/                             # app.js, validation.js, charts.js
```

---

## 🗄️ Database Setup Instructions

### Step 1: Start MySQL Server
Make sure your local MySQL 8.0+ server is running:
```bash
# macOS with Homebrew:
brew services start mysql

# Or on Linux:
sudo systemctl start mysql
```

### Step 2: Initialize Database Schema and Seed Data
Execute `schema.sql` and `seed.sql` using your MySQL command line client:
```bash
# Create database and tables:
mysql -u root -p < sql/schema.sql

# Insert initial administrator, test user, categories, and sample expenses:
mysql -u root -p < sql/seed.sql
```

### Step 3: Configure Database Credentials
Edit `src/main/resources/db.properties` (or copy from `db.properties.example`):
```properties
db.url=jdbc:mysql://localhost:3306/finance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=your_mysql_password
db.driver=com.mysql.cj.jdbc.Driver

# Secret key for AES-256 expense note encryption
security.aes.key=MySuperSecretFinKey2026!Finance
```

### Step 4: Verify Database Connection
Run the standalone verification test utility:
```bash
mvn compile exec:java -Dexec.mainClass="com.finance.util.ConnectionTester"
```

---

## 🚢 How to Build & Run on Apache Tomcat 10.1

### Step 1: Package the Application WAR
Run the Maven build command:
```bash
mvn clean package -DskipTests
```
This generates `target/finance-platform.war`.

### Step 2: Deploy to Apache Tomcat 10.1
1. Download and extract **Apache Tomcat 10.1.x** (which supports `jakarta.servlet 6.0`).
2. Copy `target/finance-platform.war` into your Tomcat `webapps/` directory:
   ```bash
   cp target/finance-platform.war /path/to/tomcat10/webapps/
   ```
3. Start Tomcat:
   ```bash
   # On macOS/Linux:
   /path/to/tomcat10/bin/startup.sh

   # On Windows:
   C:\path\to\tomcat10\bin\startup.bat
   ```

### Step 3: Access in Web Browser
Navigate to:
```
http://localhost:8080/finance-platform/
```
The root URL automatically routes to `/login`.

---

## 🔐 Default Login Credentials

Both default test accounts are pre-seeded with active BCrypt hashed passwords:

| Role | Email Address | Password | Privileges |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@finance.com` | `Admin@123` | Full access: User management, security audit logs, system preferences |
| **Regular User** | `user@finance.com` | `User@123` | Personal dashboard, expense logging, budget caps, savings targets |

*(Quick-fill buttons are provided directly on the login screen for convenient evaluation)*

---

## 🎨 Design System & Themes

- **Light Mode**: Clean white cards (`#ffffff`), slate borders (`#e2e8f0`), soft shadows, and emerald green primary accents (`#059669`).
- **Dark Mode**: True black canvas (`#09090b`), charcoal cards (`#141418`), and glowing emerald green accents (`#10b981`).
- **State Persistence**: Theme preference is automatically remembered across browser tabs via `localStorage`.
- **Responsive Layout**: Sidebar collapses into a mobile slide-out drawer on viewports under 768px.

---

