package com.luigi.lifeos.feature.onboarding.ui
import org.junit.Assert.*
import org.junit.Test
class OnboardingValidationTest {
    @Test fun rejectsMalformedAndInconsistentBudgets() {
        val base = OnboardingUiState(step = OnboardingStep.BUDGET)
        assertTrue(base.canAdvance)
        assertFalse(base.copy(monthlyBudgetText = "1..2").canAdvance)
        assertFalse(base.copy(monthlyBudgetText = "100", foodBudgetText = "101").canAdvance)
        assertTrue(base.copy(monthlyBudgetText = "100,29", foodBudgetText = "50").canAdvance)
    }
    @Test fun validatesOptionalMetricsAndRequiredName() {
        assertFalse(OnboardingUiState(step = OnboardingStep.PROFILE, displayName = " ").canAdvance)
        assertFalse(OnboardingUiState(step = OnboardingStep.PROFILE, displayName = "Demo", ageText = "999").canAdvance)
        assertFalse(OnboardingUiState(step = OnboardingStep.BODY, weightKgText = "0").canAdvance)
        assertFalse(OnboardingUiState(step = OnboardingStep.BODY, weightKgText = "NaN").canAdvance)
        assertTrue(OnboardingUiState(step = OnboardingStep.BODY).canAdvance)
    }
}
