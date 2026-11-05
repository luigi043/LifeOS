package com.luigi.lifeos.feature.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luigi.lifeos.core.designsystem.component.LifeOsCard
import com.luigi.lifeos.core.designsystem.component.LifeOsPrimaryButton
import com.luigi.lifeos.core.designsystem.component.LifeOsSecondaryButton
import com.luigi.lifeos.core.designsystem.component.LifeOsSectionHeader
import com.luigi.lifeos.core.designsystem.component.LifeOsTextField
import com.luigi.lifeos.core.designsystem.theme.ScreenHorizontalPadding
import com.luigi.lifeos.core.designsystem.theme.ScreenTopPadding
import com.luigi.lifeos.core.designsystem.theme.Spacing
import com.luigi.lifeos.core.domain.model.ActivityLevel
import com.luigi.lifeos.core.domain.model.DietaryPreference
import com.luigi.lifeos.core.domain.model.FitnessGoal

/**
 * Multi-step onboarding. Renders one of six steps based on
 * [OnboardingUiState.step]. The progress bar at the top is a visible
 * "you're almost there" — completion rate is the metric onboarding lives
 * or dies on.
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val event by viewModel.events.collectAsState()

    LaunchedEffect(event) {
        if (event is OnboardingEvent.Finished) {
            viewModel.consumeEvent()
            onFinished()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            // Step progress
            LinearProgressIndicator(
                progress = { state.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = ScreenHorizontalPadding,
                        vertical = ScreenTopPadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    when (state.step) {
                        OnboardingStep.WELCOME -> Column(Modifier.verticalScroll(rememberScrollState())) { WelcomeStep() }
                        OnboardingStep.PROFILE -> Column(Modifier.verticalScroll(rememberScrollState())) { ProfileStep(state, viewModel) }
                        OnboardingStep.BODY -> Column(Modifier.verticalScroll(rememberScrollState())) { BodyStep(state, viewModel) }
                        OnboardingStep.GOALS -> Column(Modifier.verticalScroll(rememberScrollState())) { GoalsStep(state, viewModel) }
                        OnboardingStep.BUDGET -> Column(Modifier.verticalScroll(rememberScrollState())) { BudgetStep(state, viewModel) }
                        OnboardingStep.REVIEW -> ReviewStep(state)
                    }
                }

                (state.error ?: state.validationMessage)?.let { msg ->
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                NavRow(
                    canBack = state.step != OnboardingStep.WELCOME && !state.isSaving,
                    canAdvance = state.canAdvance && !state.isSaving,
                    isSaving = state.isSaving,
                    isFinalStep = state.step == OnboardingStep.REVIEW,
                    onBack = viewModel::back,
                    onNext = viewModel::next,
                )
            }
        }
    }
}

@Composable
private fun NavRow(
    canBack: Boolean,
    canAdvance: Boolean,
    isSaving: Boolean,
    isFinalStep: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (canBack) {
            LifeOsSecondaryButton(
                text = "Back",
                onClick = onBack,
                modifier = Modifier.weight(1f),
            )
        }
        LifeOsPrimaryButton(
            text = when {
                isSaving -> "Saving..."
                isFinalStep -> "Finish"
                else -> "Continue"
            },
            onClick = onNext,
            enabled = canAdvance,
            modifier = Modifier.weight(if (canBack) 1f else 1f),
        )
    }
}

// ---------- Steps ----------

@Composable
private fun WelcomeStep() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        LifeOsSectionHeader(
            title = "Welcome to LifeOS",
            subtitle = "Set up your local profile and start tracking expenses. Other lifestyle modules are in development.",
        )
        Spacer(Modifier.height(Spacing.md))
        LifeOsCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text("What you'll do today:", fontWeight = FontWeight.SemiBold)
                BulletLine("Tell us a bit about you.")
                BulletLine("Pick a fitness goal and dietary preference.")
                BulletLine("Save your starting budget preferences.")
            }
        }
    }
}

@Composable
private fun ProfileStep(state: OnboardingUiState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        LifeOsSectionHeader(
            title = "About you",
            subtitle = "We use this to personalise your dashboard.",
        )
        LifeOsTextField(
            value = state.displayName,
            onValueChange = vm::onDisplayNameChange,
            label = "Your name",
        )
        LifeOsTextField(
            value = state.ageText,
            onValueChange = vm::onAgeChange,
            label = "Age (optional)",
            keyboardType = KeyboardType.Number,
        )
    }
}

@Composable
private fun BodyStep(state: OnboardingUiState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        LifeOsSectionHeader(
            title = "Your body",
            subtitle = "Optional profile details. Enter positive numbers or leave blank. Targets are not calculated yet.",
        )
        LifeOsTextField(
            value = state.heightCmText,
            onValueChange = vm::onHeightChange,
            label = "Height (cm)",
            keyboardType = KeyboardType.Decimal,
        )
        LifeOsTextField(
            value = state.weightKgText,
            onValueChange = vm::onWeightChange,
            label = "Current weight (kg)",
            keyboardType = KeyboardType.Decimal,
        )
        LifeOsTextField(
            value = state.goalWeightKgText,
            onValueChange = vm::onGoalWeightChange,
            label = "Goal weight (kg)",
            keyboardType = KeyboardType.Decimal,
        )
    }
}

@Composable
private fun GoalsStep(state: OnboardingUiState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        LifeOsSectionHeader(
            title = "Your goals",
            subtitle = "Save your preferences for future planning features.",
        )
        ChipGroup(
            label = "Fitness goal",
            options = FitnessGoal.entries,
            selected = state.fitnessGoal,
            labelFor = { it.prettyLabel() },
            onSelected = vm::onFitnessGoalChange,
        )
        ChipGroup(
            label = "Activity level",
            options = ActivityLevel.entries,
            selected = state.activityLevel,
            labelFor = { it.prettyLabel() },
            onSelected = vm::onActivityLevelChange,
        )
        ChipGroup(
            label = "Diet",
            options = DietaryPreference.entries,
            selected = state.dietaryPreference,
            labelFor = { it.prettyLabel() },
            onSelected = vm::onDietaryPreferenceChange,
        )
    }
}

@Composable
private fun BudgetStep(state: OnboardingUiState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        LifeOsSectionHeader(
            title = "Your budget",
            subtitle = "Optional preferences. Use up to two decimal places. Food budget must not exceed the monthly budget.",
        )
        LifeOsTextField(
            value = state.monthlyBudgetText,
            onValueChange = vm::onMonthlyBudgetChange,
            label = "Monthly budget",
            keyboardType = KeyboardType.Decimal,
            supportingText = "Total you can spend each month.",
        )
        LifeOsTextField(
            value = state.foodBudgetText,
            onValueChange = vm::onFoodBudgetChange,
            label = "Food budget",
            keyboardType = KeyboardType.Decimal,
            supportingText = "Saved to your profile. Grocery warnings are not available yet.",
        )
        ChipGroup(
            label = "Currency",
            options = listOf("EUR", "USD", "GBP", "BRL"),
            selected = state.currency,
            labelFor = { it },
            onSelected = vm::onCurrencyChange,
        )
    }
}

@Composable
private fun ReviewStep(state: OnboardingUiState) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(bottom = Spacing.md),
    ) {
        item {
            LifeOsSectionHeader(
                title = "All set?",
                subtitle = "Saved locally on this device. Android backup follows your device settings.",
            )
        }
        item { ReviewLine("Name", state.displayName.ifBlank { "—" }) }
        item { ReviewLine("Age", state.ageText.ifBlank { "—" }) }
        item { ReviewLine("Height", state.heightCmText.formatOrDash(" cm")) }
        item { ReviewLine("Weight", state.weightKgText.formatOrDash(" kg")) }
        item { ReviewLine("Goal weight", state.goalWeightKgText.formatOrDash(" kg")) }
        item { ReviewLine("Fitness goal", state.fitnessGoal.prettyLabel()) }
        item { ReviewLine("Activity", state.activityLevel.prettyLabel()) }
        item { ReviewLine("Diet", state.dietaryPreference.prettyLabel()) }
        item { ReviewLine("Monthly budget", state.monthlyBudgetText.formatOrDash(" ${state.currency}")) }
        item { ReviewLine("Food budget", state.foodBudgetText.formatOrDash(" ${state.currency}")) }
    }
}

// ---------- Helpers ----------

@Composable
private fun <T> ChipGroup(
    label: String,
    options: List<T>,
    selected: T,
    labelFor: (T) -> String,
    onSelected: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        // FlowRow would be ideal here, but we keep it portable with a simple
        // LazyRow-style wrap via Column of Rows; for short lists this is fine.
        WrapRow(
            items = options,
            spacing = Spacing.sm,
        ) { opt ->
            FilterChip(
                selected = opt == selected,
                onClick = { onSelected(opt) },
                label = { Text(labelFor(opt)) },
            )
        }
    }
}

@Composable
private fun <T> WrapRow(
    items: List<T>,
    spacing: androidx.compose.ui.unit.Dp,
    content: @Composable (T) -> Unit,
) {
    // Cheap wrap: 2 chips per row max. Real FlowRow can come from
    // accompanist or M3 later — kept simple for Phase 1.
    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
        items.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                rowItems.forEach { content(it) }
            }
        }
    }
}

@Composable
private fun BulletLine(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•  ", fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ReviewLine(label: String, value: String) {
    LifeOsCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(Spacing.md)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Medium)
        }
    }
}

private fun String.formatOrDash(suffix: String): String =
    if (isBlank()) "—" else "$this$suffix"

private fun FitnessGoal.prettyLabel(): String = when (this) {
    FitnessGoal.LOSE_WEIGHT -> "Lose weight"
    FitnessGoal.GAIN_MUSCLE -> "Gain muscle"
    FitnessGoal.MAINTAIN -> "Maintain"
    FitnessGoal.GENERAL_HEALTH -> "General health"
}

private fun ActivityLevel.prettyLabel(): String = when (this) {
    ActivityLevel.SEDENTARY -> "Sedentary"
    ActivityLevel.LIGHT -> "Light"
    ActivityLevel.MODERATE -> "Moderate"
    ActivityLevel.HIGH -> "High"
    ActivityLevel.ATHLETE -> "Athlete"
}

private fun DietaryPreference.prettyLabel(): String = when (this) {
    DietaryPreference.OMNIVORE -> "Omnivore"
    DietaryPreference.PESCATARIAN -> "Pescatarian"
    DietaryPreference.VEGETARIAN -> "Vegetarian"
    DietaryPreference.VEGAN -> "Vegan"
    DietaryPreference.KETO -> "Keto"
    DietaryPreference.MEDITERRANEAN -> "Mediterranean"
}
