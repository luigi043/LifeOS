package com.luigi.lifeos.core.domain.model

/**
 * "Daily Stability Score" from the spec — the headline cross-system metric.
 *
 * Each contributor is a 0..1 weight × 0..1 value. Weights are normalised so
 * the final score lands in 0..100. Tuning the weights is the entry point for
 * personalisation later (e.g. user disabling sleep tracking → drop that weight).
 *
 * Why this lives in :core:domain: the score is computed across multiple
 * feature modules' data — keeping the formula here avoids any one feature
 * "owning" it.
 */
data class StabilityScore(
    val value: Int, // 0..100
    val contributors: List<Contributor>,
) {
    init {
        require(value in 0..100) { "StabilityScore must be 0..100, got $value" }
    }

    data class Contributor(
        val label: String,         // "Workout", "Hydration", "Sleep", ...
        val achieved: Double,      // 0.0..1.0
        val weight: Double,        // 0.0..1.0
    )

    companion object {
        fun calculate(contributors: List<Contributor>): StabilityScore {
            if (contributors.isEmpty()) return StabilityScore(0, emptyList())
            val totalWeight = contributors.sumOf { it.weight }
            if (totalWeight == 0.0) return StabilityScore(0, contributors)
            val weighted = contributors.sumOf { it.achieved.coerceIn(0.0, 1.0) * it.weight } / totalWeight
            return StabilityScore((weighted * 100).toInt(), contributors)
        }
    }
}
