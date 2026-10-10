# Layered MVC System Architecture

This document describes the software architecture, data flow, and grading rubric components of the **Online Personal Finance Management Platform**.

```mermaid
flowchart TD
    subgraph ClientLayer ["Presentation Layer (Client Browser)"]
        UI["Bootstrap 5 + Chart.js + Vanilla JS"]
        AuthUI["Login / Register (Live Strength Meter)"]
        DashUI["Dashboard (Cards, Doughnut & Trend Charts)"]
        UserUI["Expenses, Budgets & Goals Modals"]
        AdminUI["User Management & Security Log Tables"]
    end

    subgraph SecurityFilterLayer ["Security & Interception Layer"]
        CF["CsrfFilter (Validates per-session Token)"]
        AF["AuthFilter (Protects /user/* & /admin/*)"]
        ADF["AdminFilter (Guards /admin/* with 403)"]
    end

    subgraph ControllerLayer ["Web MVC Controller Layer (@WebServlet)"]
        AuthS["AuthServlet (/login, /register, /logout)"]
        DashS["UserDashboardServlet (/user/dashboard)"]
        ExpS["ExpenseServlet (/user/expenses)"]
        BudS["BudgetServlet (/user/budgets)"]
        GoalS["GoalServlet (/user/goals)"]
        AdminS["AdminUserServlet & AdminSecurityServlet"]
    end

    subgraph ServiceLayer ["Business Logic & Concurrency Layer"]
        UserServ["UserService (BCrypt Hashing)"]
        ExpServ["ExpenseService (Aggregations)"]
        BudServ["BudgetService (Limit Checking)"]
        GoalServ["GoalService (Progress Tracking)"]
        AdminServ["AdminService (Role Guards)"]
        RepServ["ReportService (ExecutorService Thread Pool)"]
        Throttler["LoginThrottler (ConcurrentHashMap Lockout)"]
        Logger["AuditLogger (Synchronized Mutex Logging)"]
    end

    subgraph DAOLayer ["Data Access Layer (BaseDAO&lt;T&gt;)"]
        UserDAO["UserDAO"]
        CategoryDAO["CategoryDAO"]
        ExpenseDAO["ExpenseDAO (AES Encrypt/Decrypt)"]
        BudgetDAO["BudgetDAO (Correlated Spent Queries)"]
        GoalDAO["GoalDAO"]
        SecurityDAO["SecurityLogDAO"]
        SettingDAO["SystemSettingDAO"]
    end

    subgraph PersistenceLayer ["Database & Infrastructure"]
        ConnFactory["DBConnection (JDBC Connection Factory)"]
        MySQL[("MySQL 8.0 Engine (finance_db)")]
    end

    %% Request & Response Connections
    ClientLayer -->|HTTP GET / POST| SecurityFilterLayer
    SecurityFilterLayer -->|Authorized Requests| ControllerLayer
    ControllerLayer -->|Dispatches Business Tasks| ServiceLayer
    ServiceLayer -->|Multithreaded Worker Jobs| RepServ
    ServiceLayer -->|Thread-Safe Auditing| Logger
    ServiceLayer -->|Failed Login Throttle| Throttler
    ServiceLayer -->|Domain Entity Operations| DAOLayer
    DAOLayer -->|PreparedStatement &amp; try-with-resources| ConnFactory
    ConnFactory -->|Pure JDBC TCP Connection| MySQL
```

---

## Layer Responsibilities

1. **Presentation Layer (JSP & Static Assets)**:
   - Uses Jakarta Standard Tag Library (`jakarta.tags.core`, `jakarta.tags.fmt`) for server-side HTML rendering.
   - Mobile-first responsive UI using Bootstrap 5, Bootstrap Icons, and Chart.js.
   - Interactive theme engine providing white & green in light mode and black & green in dark mode (saved in `localStorage`).

2. **Filter & Security Layer**:
   - `CsrfFilter`: Verifies state-changing HTTP POST operations using constant-time token comparison.
   - `AuthFilter`: Enforces 15-minute session timeouts and redirects unauthenticated users to `/login`.
   - `AdminFilter`: Restricts `/admin/*` operations exclusively to users with `ADMIN` privileges.

3. **Controller Layer (Servlets)**:
   - Implements the **Post-Redirect-Get (PRG)** design pattern to eliminate duplicate form resubmissions.
   - Stores user confirmation feedback in session flash attributes (`flashSuccess`, `flashError`, `flashWarning`).

4. **Service & Concurrency Layer**:
   - `ReportService`: Asynchronously compiles CSV exports and summary reports using an `ExecutorService` thread pool.
   - `AuditLogger`: Uses `synchronized` methods to ensure atomic, non-interleaved writes to security logs.
   - `LoginThrottler`: Manages failed login attempts in a thread-safe `ConcurrentHashMap` to lock accounts after 5 failures.

5. **Data Access Layer (DAOs)**:
   - Extends the generic `BaseDAO<T>` interface.
   - Uses exclusively `PreparedStatement` within Java `try-with-resources` blocks to eliminate SQL injection and connection leaks.
