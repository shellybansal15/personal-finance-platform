package com.finance.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Formats monetary amounts in Indian Rupees using the en-IN locale
 * (e.g. ₹1,25,000.50). NumberFormat is not thread-safe, so each call
 * uses a ThreadLocal instance for the currency symbol; Indian digit
 * grouping (last 3, then pairs of 2) is applied explicitly for stable
 * output across JDK versions.
 */
public final class CurrencyUtil {

    private static final Locale EN_IN = Locale.forLanguageTag("en-IN");

    private static final ThreadLocal<NumberFormat> CURRENCY = ThreadLocal.withInitial(() ->
            NumberFormat.getCurrencyInstance(EN_IN));

    private CurrencyUtil() {
    }

    public static String format(double amount) {
        NumberFormat nf = CURRENCY.get();
        Currency currency = nf.getCurrency();
        String symbol = currency != null ? currency.getSymbol(EN_IN) : "₹";
        if (symbol == null || symbol.isBlank() || "INR".equalsIgnoreCase(symbol)) {
            symbol = "₹";
        }

        boolean negative = amount < 0;
        long paise = Math.round(Math.abs(amount) * 100.0);
        long rupees = paise / 100;
        long frac = paise % 100;
        String formatted = symbol + indianGrouping(rupees) + "." + String.format("%02d", frac);
        return negative ? "-" + formatted : formatted;
    }

    public static String format(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return format(0);
        }
        try {
            return format(Double.parseDouble(amountStr.trim()));
        } catch (NumberFormatException e) {
            return "₹" + amountStr;
        }
    }

    /**
     * Indian numbering: groups of 2 after the first (rightmost) group of 3.
     * Examples: 125000 → 1,25,000 ; 4500 → 4,500 ; 50 → 50
     */
    private static String indianGrouping(long value) {
        String digits = Long.toString(Math.abs(value));
        if (digits.length() <= 3) {
            return digits;
        }
        String lastThree = digits.substring(digits.length() - 3);
        String remaining = digits.substring(0, digits.length() - 3);
        StringBuilder grouped = new StringBuilder();
        int i = remaining.length();
        while (i > 0) {
            if (grouped.length() > 0) {
                grouped.insert(0, ',');
            }
            int start = Math.max(0, i - 2);
            grouped.insert(0, remaining.substring(start, i));
            i = start;
        }
        return grouped.append(',').append(lastThree).toString();
    }
}
