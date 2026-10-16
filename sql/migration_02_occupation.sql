-- ============================================================================
-- Migration 02: Add occupation column to users table
-- Run this on an existing database that already has the schema applied
-- ============================================================================

USE finance_db;

ALTER TABLE users
    ADD COLUMN occupation ENUM(
        'Student',
        'Salaried Employee',
        'Self-Employed',
        'Business Owner',
        'Homemaker',
        'Retired',
        'Other'
    ) NULL DEFAULT NULL
    AFTER status;
