package com.luigi.lifeos.feature.finance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.finance.ui.AddExpenseScreen
import com.luigi.lifeos.feature.finance.ui.FinanceScreen

/** Route tokens — module-public so :app can deep-link without importing internals. */
const val FinanceRoute    = "finance"
const val AddExpenseRoute = "finance/add_expense"

/**
 * Registers the Finance overview screen.
 * [onNavigateToAddExpense] is supplied by :app's NavHost.
 */
fun NavGraphBuilder.financeScreen(onNavigateToAddExpense: () -> Unit) {
    composable(route = FinanceRoute) {
        FinanceScreen(onNavigateToAddExpense = onNavigateToAddExpense)
    }
}

/**
 * Registers the Add Expense form screen.
 * [onBack] pops the back stack (navigated to after save or on back press).
 */
fun NavGraphBuilder.addExpenseScreen(onBack: () -> Unit) {
    composable(route = AddExpenseRoute) {
        AddExpenseScreen(onBack = onBack)
    }
}

/** Convenience — navigate from any screen to the Add Expense form. */
fun NavController.navigateToAddExpense() = navigate(AddExpenseRoute)
