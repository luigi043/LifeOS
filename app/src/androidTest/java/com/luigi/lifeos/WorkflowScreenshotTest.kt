package com.luigi.lifeos

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Run on a fresh emulator install only. All values are synthetic. */
class WorkflowScreenshotTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "screenshots")
        directory.mkdirs()
        instrumentation.uiAutomation.takeScreenshot().let { bitmap ->
            File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
    }

    private fun click(text: String) = compose.onNode(hasText(text) and hasClickAction()).performScrollTo().performClick()
    private fun next() = compose.onNodeWithText("Continue").performClick()
    private fun home() {
        compose.onNodeWithContentDescription("Today", useUnmergedTree = true).performClick()
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Explore LifeOS").fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun walkthroughAndExpenseLifecycle() {
        compose.waitUntil(30_000) { compose.onAllNodesWithText("Welcome to LifeOS").fetchSemanticsNodes().isNotEmpty() }
        shot("01-onboarding-welcome")
        next()
        compose.onNodeWithText("Continue").assertIsNotEnabled()
        compose.onNodeWithText("Your name").performTextInput("Alex Demo")
        shot("02-onboarding-profile")
        next(); shot("03-onboarding-body")
        next(); shot("04-onboarding-goals")
        next()
        compose.onNodeWithText("Monthly budget").performTextInput("100")
        compose.onNodeWithText("Food budget").performTextInput("150")
        compose.onNodeWithText("Continue").assertIsNotEnabled()
        shot("05-onboarding-budget-invalid")
        compose.onNodeWithText("Food budget").performTextReplacement("50")
        shot("06-onboarding-budget")
        next(); shot("07-onboarding-review")
        compose.onNodeWithText("Finish").performClick()
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Explore LifeOS").fetchSemanticsNodes().isNotEmpty() }
        shot("08-today")

        click("Finance")
        compose.waitUntil(15_000) { compose.onAllNodesWithText("No expenses this month.\nTap + to add one.").fetchSemanticsNodes().isNotEmpty() }
        shot("09-finance-empty")
        compose.onNodeWithContentDescription("Add expense").performClick()
        shot("10-add-expense")
        click("Save Expense")
        compose.onNodeWithText("Enter a positive amount with up to two decimal places").assertExists()
        shot("11-add-expense-invalid")
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("0,29")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("Synthetic coffee")
        compose.onNode(isToggleable()).performClick()
        shot("12-add-expense-recurring")
        click("Save Expense")
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Synthetic coffee").fetchSemanticsNodes().isNotEmpty() }
        shot("13-finance-saved")
        compose.onNodeWithContentDescription("Delete expense Synthetic coffee").performClick()
        shot("14-delete-confirmation")
        compose.onNodeWithText("Cancel").performClick()
        compose.onNodeWithText("Synthetic coffee").assertExists()
        click("Income"); shot("15-finance-income")
        click("Expenses")
        compose.onNodeWithContentDescription("Delete expense Synthetic coffee").performClick()
        compose.onNodeWithText("Delete", substring = false).performClick()
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Synthetic coffee").fetchSemanticsNodes().isEmpty() }
        shot("16-finance-deleted")

        listOf("Nutrition", "Workout", "Grocery", "Mental wellness", "Habits", "Analytics", "Settings").forEachIndexed { index, label ->
            home(); click(label)
            shot("${17 + index}-${label.lowercase().replace(' ', '-')}")
        }
        compose.onNode(isToggleable()).performClick()
        compose.waitUntil(10_000) { compose.onAllNodes(isOn()).fetchSemanticsNodes().isNotEmpty() }
        shot("24-settings-dark")
        compose.onNode(isToggleable()).performClick()
        compose.waitUntil(10_000) { compose.onAllNodes(isOff()).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithContentDescription("Profile", useUnmergedTree = true).performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Alex Demo").fetchSemanticsNodes().isNotEmpty() }
        shot("25-profile")
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Explore LifeOS").fetchSemanticsNodes().isNotEmpty() }
        shot("26-back-to-today")
        compose.activityRule.scenario.recreate()
        compose.waitUntil(15_000) { compose.onAllNodesWithContentDescription("Today", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Welcome to LifeOS").assertDoesNotExist()
        shot("27-relaunch")
    }
}
