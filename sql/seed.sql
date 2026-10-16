-- ============================================================================
-- Online Personal Finance Management Platform
-- Seed Data: Demo users, categories, INR expenses, budgets, goals, logs & settings
-- Passwords (Fill buttons on login.jsp):
--   Admin: Admin@123 -> $2a$10$TeuGs1SqL6BgDJ3tVKUXpuqsNcNZGKSLS6OUTqBTH9JkNnEPYonci
--   User:  User@123  -> $2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy
-- Notes: plaintext is OK — EncryptionUtil.decrypt() returns as-is if not ciphertext.
-- Runtime auto-seed: com.finance.util.DatabaseSeeder (ServletContextListener + demo login)
-- ============================================================================

USE finance_db;

-- ----------------------------------------------------------------------------
-- 1. Demo + sample users (occupation)
-- ----------------------------------------------------------------------------
INSERT INTO users (id, name, email, password_hash, role, status, occupation, created_at)
VALUES
(1, 'System Administrator', 'admin@finance.com', '$2a$10$TeuGs1SqL6BgDJ3tVKUXpuqsNcNZGKSLS6OUTqBTH9JkNnEPYonci', 'ADMIN', 'ACTIVE', 'Salaried Employee', NOW()),
(2, 'Arjun Sharma', 'user@finance.com', '$2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy', 'USER', 'ACTIVE', 'Salaried Employee', NOW()),
(3, 'Priya Patel', 'priya.patel@example.com', '$2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy', 'USER', 'ACTIVE', 'Self-Employed', NOW()),
(4, 'Rahul Verma', 'rahul.verma@example.com', '$2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy', 'USER', 'ACTIVE', 'Student', NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name), occupation=VALUES(occupation);

-- ----------------------------------------------------------------------------
-- 2. Categories (Food, Bills, Shopping, Travel + supporting)
-- ----------------------------------------------------------------------------
INSERT INTO categories (id, name, type) VALUES
(1, 'Food & Dining', 'EXPENSE'),
(2, 'Rent & Housing', 'EXPENSE'),
(3, 'Utilities & Bills', 'EXPENSE'),
(4, 'Transportation', 'EXPENSE'),
(5, 'Entertainment & Leisure', 'EXPENSE'),
(6, 'Healthcare & Medical', 'EXPENSE'),
(7, 'Shopping & Groceries', 'EXPENSE'),
(8, 'Salary & Freelancing', 'INCOME'),
(9, 'Travel', 'EXPENSE')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ----------------------------------------------------------------------------
-- 3. Rich INR expenses for user@finance.com (id=2) — ~90 days for monthly trends
-- Category map: 1=Food, 3=Bills, 7=Shopping, 9=Travel (+ rent/transport/etc.)
-- ----------------------------------------------------------------------------
INSERT INTO expenses (id, user_id, category_id, amount, expense_date, note, payment_mode, created_at) VALUES
(1,  2, 1, 450.50,  DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY),  'Swiggy lunch — paneer wrap', 'UPI', NOW()),
(2,  2, 3, 850.00,  DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY),  'Electricity bill — BESCOM', 'NET_BANKING', NOW()),
(3,  2, 1, 289.00,  DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY),  'Café coffee & croissant', 'UPI', NOW()),
(4,  2, 7, 1207.75, DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY),  'Amazon — headphones & cables', 'CREDIT_CARD', NOW()),
(5,  2, 9, 3200.00, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY),  'IRCTC Mumbai weekend tickets', 'NET_BANKING', NOW()),
(6,  2, 1, 1250.00, DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY),  'Weekend dinner at Barbeque Nation', 'CREDIT_CARD', NOW()),
(7,  2, 4, 320.00,  DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY),  'Metro card recharge', 'DEBIT_CARD', NOW()),
(8,  2, 3, 599.00,  DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY),  'Airtel broadband fibre', 'UPI', NOW()),
(9,  2, 5, 450.00,  DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY),  'PVR movie night', 'UPI', NOW()),
(10, 2, 7, 784.00,  DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), 'Myntra festive kurta', 'CREDIT_CARD', NOW()),
(11, 2, 1, 523.00,  DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), 'BigBasket groceries top-up', 'WALLET', NOW()),
(12, 2, 6, 650.00,  DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), 'Apollo pharmacy — medicines', 'DEBIT_CARD', NOW()),
(13, 2, 3, 399.00,  DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 'Jio postpaid mobile', 'UPI', NOW()),
(14, 2, 2, 18500.00,DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), 'Monthly apartment rent', 'NET_BANKING', NOW()),
(15, 2, 9, 1850.00, DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), 'Uber / Ola airport transfers', 'UPI', NOW()),
(16, 2, 1, 642.00,  DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), 'Office team lunch', 'UPI', NOW()),
(17, 2, 7, 2499.00, DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), 'Decathlon running shoes', 'DEBIT_CARD', NOW()),
(18, 2, 4, 180.00,  DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), 'Auto rickshaw to client site', 'CASH', NOW()),
(19, 2, 1, 380.00,  DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), 'Domino''s pizza night', 'UPI', NOW()),
(20, 2, 3, 921.50,  DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), 'Water + society maintenance', 'NET_BANKING', NOW()),
(21, 2, 9, 8900.00, DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), 'Indigo flight BLR → GOI', 'CREDIT_CARD', NOW()),
(22, 2, 7, 650.00,  DATE_SUB(CURRENT_DATE, INTERVAL 33 DAY), 'IKEA desk organiser', 'UPI', NOW()),
(23, 2, 1, 910.00,  DATE_SUB(CURRENT_DATE, INTERVAL 35 DAY), 'Monthly grocery haul — Nature''s Basket', 'DEBIT_CARD', NOW()),
(24, 2, 5, 1200.00, DATE_SUB(CURRENT_DATE, INTERVAL 38 DAY), 'Concert tickets — Indiranagar', 'CREDIT_CARD', NOW()),
(25, 2, 3, 1499.00, DATE_SUB(CURRENT_DATE, INTERVAL 40 DAY), 'Netflix + Spotify annual share', 'CREDIT_CARD', NOW()),
(26, 2, 9, 4500.00, DATE_SUB(CURRENT_DATE, INTERVAL 42 DAY), 'Goa hotel advance — Booking.com', 'CREDIT_CARD', NOW()),
(27, 2, 2, 18500.00,DATE_SUB(CURRENT_DATE, INTERVAL 45 DAY), 'Monthly apartment rent', 'NET_BANKING', NOW()),
(28, 2, 7, 1899.00, DATE_SUB(CURRENT_DATE, INTERVAL 45 DAY), 'Flipkart home appliances sale', 'CREDIT_CARD', NOW()),
(29, 2, 1, 275.00,  DATE_SUB(CURRENT_DATE, INTERVAL 48 DAY), 'Street-food chaat & juice', 'CASH', NOW()),
(30, 2, 4, 400.00,  DATE_SUB(CURRENT_DATE, INTERVAL 50 DAY), 'Petrol top-up', 'DEBIT_CARD', NOW()),
(31, 2, 9, 680.00,  DATE_SUB(CURRENT_DATE, INTERVAL 52 DAY), 'Local sightseeing cab day pass', 'CASH', NOW()),
(32, 2, 3, 750.00,  DATE_SUB(CURRENT_DATE, INTERVAL 55 DAY), 'LPG cylinder refill', 'UPI', NOW()),
(33, 2, 7, 420.00,  DATE_SUB(CURRENT_DATE, INTERVAL 58 DAY), 'Stationery & printer paper', 'CASH', NOW()),
(34, 2, 6, 1500.00, DATE_SUB(CURRENT_DATE, INTERVAL 60 DAY), 'Annual health checkup', 'NET_BANKING', NOW()),
(35, 2, 1, 1100.00, DATE_SUB(CURRENT_DATE, INTERVAL 62 DAY), 'Anniversary dinner', 'CREDIT_CARD', NOW()),
(36, 2, 9, 2100.00, DATE_SUB(CURRENT_DATE, INTERVAL 68 DAY), 'MakeMyTrip bus + snacks', 'UPI', NOW()),
(37, 2, 3, 2100.00, DATE_SUB(CURRENT_DATE, INTERVAL 70 DAY), 'Quarterly electricity settlement', 'NET_BANKING', NOW()),
(38, 2, 5, 650.00,  DATE_SUB(CURRENT_DATE, INTERVAL 72 DAY), 'Board-game café evening', 'UPI', NOW()),
(39, 2, 2, 18500.00,DATE_SUB(CURRENT_DATE, INTERVAL 75 DAY), 'Monthly apartment rent', 'NET_BANKING', NOW()),
(40, 2, 1, 495.00,  DATE_SUB(CURRENT_DATE, INTERVAL 75 DAY), 'Lunch thali near office', 'UPI', NOW()),
(41, 2, 7, 3200.00, DATE_SUB(CURRENT_DATE, INTERVAL 80 DAY), 'Nike hoodie — offline store', 'DEBIT_CARD', NOW()),
(42, 2, 9, 5500.00, DATE_SUB(CURRENT_DATE, INTERVAL 85 DAY), 'Family trip hotel deposit', 'NET_BANKING', NOW())
ON DUPLICATE KEY UPDATE amount=VALUES(amount), payment_mode=VALUES(payment_mode), note=VALUES(note);

-- ----------------------------------------------------------------------------
-- 4. Budget limits for user 2 (current month, INR)
-- ----------------------------------------------------------------------------
INSERT INTO budgets (id, user_id, category_id, amount, start_date, end_date, created_at) VALUES
(1, 2, 1, 8000.00,  DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW()),
(2, 2, 3, 5000.00,  DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW()),
(3, 2, 7, 6000.00,  DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW()),
(4, 2, 9, 15000.00, DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW()),
(5, 2, 4, 2500.00,  DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW()),
(6, 2, 5, 3000.00,  DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW())
ON DUPLICATE KEY UPDATE amount=VALUES(amount);

-- ----------------------------------------------------------------------------
-- 5. Savings goals for user 2 (INR)
-- ----------------------------------------------------------------------------
INSERT INTO goals (id, user_id, description, target_amount, saved_amount, deadline, status, created_at) VALUES
(1, 2, 'Emergency Fund (6 Months)', 300000.00, 185000.00, DATE_ADD(CURRENT_DATE, INTERVAL 6 MONTH), 'IN_PROGRESS', NOW()),
(2, 2, 'New MacBook Pro M4', 180000.00, 145000.00, DATE_ADD(CURRENT_DATE, INTERVAL 3 MONTH), 'IN_PROGRESS', NOW()),
(3, 2, 'Goa Vacation Trip', 50000.00, 50000.00, DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), 'COMPLETED', NOW()),
(4, 2, 'Diwali Shopping Buffer', 25000.00, 12000.00, DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH), 'IN_PROGRESS', NOW())
ON DUPLICATE KEY UPDATE target_amount=VALUES(target_amount), saved_amount=VALUES(saved_amount);

-- ----------------------------------------------------------------------------
-- 6. System security logs (admin workspace)
-- ----------------------------------------------------------------------------
INSERT INTO security_logs (id, user_id, action, ip_address, created_at) VALUES
(1,  1, 'Admin logged into system', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2,  2, 'User logged into system', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3,  2, 'Expense record created (demo seed)', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(4,  1, 'Viewed security audit trail', '10.0.0.12', DATE_SUB(NOW(), INTERVAL 25 MINUTE)),
(5,  1, 'Updated system setting: platform_currency', '10.0.0.12', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(6,  2, 'Budget limit created for Food & Dining', '192.168.1.45', DATE_SUB(NOW(), INTERVAL 18 MINUTE)),
(7,  NULL, 'Failed login (user not found): unknown@example.com', '103.21.244.8', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
(8,  2, 'User logged in successfully as USER', '192.168.1.45', DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
(9,  1, 'User management: listed all accounts', '10.0.0.12', DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
(10, 1, 'Admin logged in successfully as ADMIN', '10.0.0.12', DATE_SUB(NOW(), INTERVAL 2 MINUTE))
ON DUPLICATE KEY UPDATE action=VALUES(action);

-- ----------------------------------------------------------------------------
-- 7. Default system settings
-- ----------------------------------------------------------------------------
INSERT INTO system_settings (id, setting_key, setting_value, updated_by, updated_at) VALUES
(1,  'allow_registration',         'true',    1, NOW()),
(2,  'max_login_attempts',          '5',       1, NOW()),
(3,  'session_timeout_minutes',     '15',      1, NOW()),
(4,  'maintenance_mode',            'false',   1, NOW()),
(5,  'platform_currency',           'INR',     1, NOW()),
(6,  'app_name',                    'FinanceFlow', 1, NOW()),
(7,  'date_format',                 'dd-MM-yyyy', 1, NOW()),
(8,  'items_per_page',              '10',      1, NOW()),
(9,  'default_theme',               'light',   1, NOW()),
(10, 'encrypt_expense_notes',       'true',    1, NOW()),
(11, 'mask_account_details',        'false',   1, NOW()),
(12, 'bcrypt_rounds',               '10',      1, NOW()),
(13, 'lockout_duration_minutes',    '15',      1, NOW()),
(14, 'require_strong_passwords',    'true',    1, NOW()),
(15, 'confirm_on_add',              'true',    1, NOW()),
(16, 'confirm_on_update',           'true',    1, NOW()),
(17, 'confirm_on_delete',           'true',    1, NOW()),
(18, 'prompt_before_delete',        'true',    1, NOW())
ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value);
