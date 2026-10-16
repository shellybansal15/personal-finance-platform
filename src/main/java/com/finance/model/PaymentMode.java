package com.finance.model;

/**
 * ============================================================================
 * [OOP Rubric: Enum Type]
 * Represents the payment mode used for an expense transaction.
 * Mapped to the 'payment_mode' ENUM column in the expenses table.
 * ============================================================================
 */
public enum PaymentMode {
    UPI("UPI", "bi-phone"),
    NET_BANKING("Net Banking", "bi-bank"),
    CREDIT_CARD("Credit Card", "bi-credit-card"),
    DEBIT_CARD("Debit Card", "bi-credit-card-2-front"),
    CASH("Cash", "bi-cash-coin"),
    WALLET("Wallet", "bi-wallet2"),
    CHEQUE("Cheque", "bi-journal-check"),
    OTHER("Other", "bi-three-dots");

    private final String displayName;
    private final String bootstrapIcon;

    PaymentMode(String displayName, String bootstrapIcon) {
        this.displayName = displayName;
        this.bootstrapIcon = bootstrapIcon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBootstrapIcon() {
        return bootstrapIcon;
    }

    /**
     * Safe parse that returns UPI as default if name doesn't match any enum constant.
     */
    public static PaymentMode fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UPI;
        }
        try {
            return PaymentMode.valueOf(value.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return UPI;
        }
    }
}
