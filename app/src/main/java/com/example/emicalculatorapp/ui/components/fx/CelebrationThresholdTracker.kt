package com.example.emicalculatorapp.ui.components.fx

/**
 * Manages celebration threshold state to fire confetti sparingly.
 * Only triggers on the rising edge when crossing the threshold,
 * and resets only when the value drops strictly below the threshold.
 */
class CelebrationThresholdTracker(
    val threshold: Int = 60 // 5 years in months
) {
    var hasTriggered: Boolean = false
        private set

    /**
     * Evaluates new saved months.
     * @return true ONLY once when crossing >= threshold on a rising edge.
     */
    fun update(savedMonths: Int): Boolean {
        return if (savedMonths >= threshold) {
            if (!hasTriggered) {
                hasTriggered = true
                true
            } else {
                false
            }
        } else {
            hasTriggered = false
            false
        }
    }

    fun reset() {
        hasTriggered = false
    }
}
