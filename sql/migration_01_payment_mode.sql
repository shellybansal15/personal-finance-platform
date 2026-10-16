-- ============================================================================
-- Migration 01: Add payment_mode column to expenses table
-- Run this on an existing database that already has the schema applied
-- ============================================================================

USE finance_db;

ALTER TABLE expenses
    ADD COLUMN payment_mode ENUM(
        'UPI',
        'NET_BANKING',
        'CREDIT_CARD',
        'DEBIT_CARD',
        'CASH',
        'WALLET',
        'CHEQUE',
        'OTHER'
    ) NOT NULL DEFAULT 'UPI'
    AFTER note;
