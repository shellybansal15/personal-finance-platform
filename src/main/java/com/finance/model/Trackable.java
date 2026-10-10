package com.finance.model;

/**
 * ============================================================================
 * [OOP Rubric: Interface Design]
 * Trackable interface establishes a common contract for financial entities
 * that measure current progress against a target monetary goal or limit.
 * Both Budget (spending ceiling) and Goal (savings target) implement this interface.
 * ============================================================================
 */
public interface Trackable {

    /**
     * @return The target budget ceiling or total savings goal amount.
     */
    double getTargetAmount();

    /**
     * @return The current amount spent (for budget) or saved (for goal).
     */
    double getCurrentAmount();

    /**
     * Computes completion percentage: (current / target) * 100.
     * Capped at appropriate ranges for visual progress bar rendering.
     *
     * @return Progress percentage as double (0.0 to 100.0+)
     */
    default double getProgressPercentage() {
        if (getTargetAmount() <= 0) {
            return 0.0;
        }
        return (getCurrentAmount() / getTargetAmount()) * 100.0;
    }

    /**
     * Indicates whether the trackable has reached 80% or more of its target.
     * Triggers amber warning states in the UI.
     *
     * @return true if progress is >= 80%, false otherwise
     */
    default boolean isNearLimit() {
        return getProgressPercentage() >= 80.0 && getProgressPercentage() < 100.0;
    }

    /**
     * Indicates whether the trackable has met or exceeded 100% of its target.
     * Triggers red danger state for budgets and green success for goals.
     *
     * @return true if progress is >= 100%
     */
    default boolean isExceeded() {
        return getProgressPercentage() >= 100.0;
    }
}
