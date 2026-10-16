-- ============================================================================
-- Online Personal Finance Management Platform
-- Seed Data: Initial test users, categories, expenses, budgets & settings
-- Updated: INR currency, payment_mode, occupation, comprehensive system settings
-- ============================================================================

USE finance_db;

-- ----------------------------------------------------------------------------
-- 1. Insert Initial Users (with occupation)
-- Passwords:
-- Admin: Admin@123 -> $2a$10$TeuGs1SqL6BgDJ3tVKUXpuqsNcNZGKSLS6OUTqBTH9JkNnEPYonci
-- User:  User@123  -> $2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy
-- ----------------------------------------------------------------------------
INSERT INTO users (id, name, email, password_hash, role, status, occupation, created_at)
VALUES 
(1, 'System Administrator', 'admin@finance.com', '$2a$10$TeuGs1SqL6BgDJ3tVKUXpuqsNcNZGKSLS6OUTqBTH9JkNnEPYonci', 'ADMIN', 'ACTIVE', 'Salaried Employee', NOW()),
(2, 'Arjun Sharma', 'user@finance.com', '$2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy', 'USER', 'ACTIVE', 'Salaried Employee', NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name), occupation=VALUES(occupation);

-- ----------------------------------------------------------------------------
-- 2. Insert 8 Standard Categories
-- ----------------------------------------------------------------------------
INSERT INTO categories (id, name, type) VALUES
(1, 'Food & Dining', 'EXPENSE'),
(2, 'Rent & Housing', 'EXPENSE'),
(3, 'Utilities & Bills', 'EXPENSE'),
(4, 'Transportation', 'EXPENSE'),
(5, 'Entertainment & Leisure', 'EXPENSE'),
(6, 'Healthcare & Medical', 'EXPENSE'),
(7, 'Shopping & Groceries', 'EXPENSE'),
(8, 'Salary & Freelancing', 'INCOME')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ----------------------------------------------------------------------------
-- 3. Insert 15 Sample Expenses for User 2 (Arjun Sharma)
-- Notes are AES-256 encrypted using default key for demonstration
-- Amounts in Indian Rupees (₹), realistic mix of payment modes
-- ----------------------------------------------------------------------------
INSERT INTO expenses (id, user_id, category_id, amount, expense_date, note, payment_mode, created_at) VALUES
(1,  2, 1, 450.50,  DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY),  'BbsECTSL3S8PLOdLT4Cb2QMpC4MOWC8T85Cl0kXl798JFWnUD+PPNkJSXi2KFczfDUqYb6wtUFEuRDAsGEzEYA==', 'UPI', NOW()),
(2,  2, 4, 320.00,  DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY),  '85X/vC6a71f0sM0z45+Qtw==', 'DEBIT_CARD', NOW()),
(3,  2, 7, 1207.75, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY),  'BbsECTSL3S8PLOdLT4Cb2QMpC4MOWC8T85Cl0kXl798JFWnUD+PPNkJSXi2KFczfDUqYb6wtUFEuRDAsGEzEYA==', 'CREDIT_CARD', NOW()),
(4,  2, 1, 289.00,  DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY),  'gH60XmD9Q6fHkR8y19/6YQ==', 'UPI', NOW()),
(5,  2, 3, 850.00,  DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), 'sTjQ+a9h8+381k7bE9jUgw==', 'NET_BANKING', NOW()),
(6,  2, 5, 245.00,  DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), '2y9kHq7P2o1n5Lm1vO4b2g==', 'UPI', NOW()),
(7,  2, 2, 8500.00, DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), 'kL7wP9x1M2b5V6z8+1Q4rg==', 'NET_BANKING', NOW()),
(8,  2, 4, 180.00,  DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), '85X/vC6a71f0sM0z45+Qtw==', 'CASH', NOW()),
(9,  2, 6, 650.00,  DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), 'xQ1lM8v2K9b4P7z1+8O3qw==', 'DEBIT_CARD', NOW()),
(10, 2, 1, 523.00,  DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), 'BbsECTSL3S8PLOdLT4Cb2QMpC4MOWC8T85Cl0kXl798JFWnUD+PPNkJSXi2KFczfDUqYb6wtUFEuRDAsGEzEYA==', 'WALLET', NOW()),
(11, 2, 7, 784.00,  DATE_SUB(CURRENT_DATE, INTERVAL 35 DAY), 'BbsECTSL3S8PLOdLT4Cb2QMpC4MOWC8T85Cl0kXl798JFWnUD+PPNkJSXi2KFczfDUqYb6wtUFEuRDAsGEzEYA==', 'CREDIT_CARD', NOW()),
(12, 2, 3, 921.50,  DATE_SUB(CURRENT_DATE, INTERVAL 45 DAY), 'sTjQ+a9h8+381k7bE9jUgw==', 'NET_BANKING', NOW()),
(13, 2, 5, 450.00,  DATE_SUB(CURRENT_DATE, INTERVAL 60 DAY), '2y9kHq7P2o1n5Lm1vO4b2g==', 'UPI', NOW()),
(14, 2, 1, 642.00,  DATE_SUB(CURRENT_DATE, INTERVAL 75 DAY), 'gH60XmD9Q6fHkR8y19/6YQ==', 'CASH', NOW()),
(15, 2, 4, 400.00,  DATE_SUB(CURRENT_DATE, INTERVAL 90 DAY), '85X/vC6a71f0sM0z45+Qtw==', 'DEBIT_CARD', NOW())
ON DUPLICATE KEY UPDATE amount=VALUES(amount), payment_mode=VALUES(payment_mode);

-- ----------------------------------------------------------------------------
-- 4. Insert Sample Budgets for User 2 (in INR)
-- ----------------------------------------------------------------------------
INSERT INTO budgets (id, user_id, category_id, amount, start_date, end_date, created_at) VALUES
(1, 2, 1, 3500.00, DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW()),
(2, 2, 4, 1500.00, DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW()),
(3, 2, 7, 2000.00, DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), LAST_DAY(CURRENT_DATE), NOW())
ON DUPLICATE KEY UPDATE amount=VALUES(amount);

-- ----------------------------------------------------------------------------
-- 5. Insert Sample Goals for User 2 (in INR)
-- ----------------------------------------------------------------------------
INSERT INTO goals (id, user_id, description, target_amount, saved_amount, deadline, status, created_at) VALUES
(1, 2, 'Emergency Fund (6 Months)', 300000.00, 185000.00, DATE_ADD(CURRENT_DATE, INTERVAL 6 MONTH), 'IN_PROGRESS', NOW()),
(2, 2, 'New MacBook Pro M4', 180000.00, 145000.00, DATE_ADD(CURRENT_DATE, INTERVAL 3 MONTH), 'IN_PROGRESS', NOW()),
(3, 2, 'Goa Vacation Trip', 50000.00, 50000.00, DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), 'COMPLETED', NOW())
ON DUPLICATE KEY UPDATE target_amount=VALUES(target_amount);

-- ----------------------------------------------------------------------------
-- 6. Insert Sample Security Logs
-- ----------------------------------------------------------------------------
INSERT INTO security_logs (id, user_id, action, ip_address, created_at) VALUES
(1, 1, 'Admin logged into system', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 2, 'User logged into system', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 2, 'Expense record created (ID #1)', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 30 MINUTE))
ON DUPLICATE KEY UPDATE action=VALUES(action);

-- ----------------------------------------------------------------------------
-- 7. Insert Default System Settings (comprehensive)
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
