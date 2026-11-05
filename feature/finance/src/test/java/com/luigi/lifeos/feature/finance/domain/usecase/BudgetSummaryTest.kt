package com.luigi.lifeos.feature.finance.domain.usecase

import com.luigi.lifeos.core.datastore.UserPreferences
import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.UserProfile
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import com.luigi.lifeos.core.domain.repository.UserProfileRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BudgetSummaryTest {
    @Test fun profileBudgetIsUsedAndDifferentCurrenciesAreNeverAddedTogether() = runTest {
        val repository = mockk<FinanceRepository>()
        val profiles = mockk<UserProfileRepository>()
        val preferences = mockk<UserPreferences>()
        val currency = MutableStateFlow("EUR")
        every { preferences.baseCurrency } returns currency
        every { profiles.observe() } returns flowOf(UserProfile(displayName = "Demo", monthlyBudgetMinor = 10_000, preferredCurrency = "EUR", createdAtEpochMillis = 0))
        every { repository.observeBudgetForPeriod(any(), any()) } returns flowOf(null)
        every { repository.observeCategories() } returns flowOf(emptyList())
        every { repository.observeExpensesByPeriod(any(), any()) } returns flowOf(listOf(
            Expense(amountMinor = 29, currency = "EUR", categoryId = 1, dateEpochMillis = 0, createdAtEpochMillis = 0),
            Expense(amountMinor = 50, currency = "USD", categoryId = 1, dateEpochMillis = 0, createdAtEpochMillis = 0),
        ))
        val useCase = GetBudgetSummaryUseCase(repository, preferences, profiles)
        val euro = useCase().first()
        assertEquals(29L, euro.totalSpentMinor)
        assertEquals(10_000L, euro.totalBudgetMinor)
        currency.value = "USD"
        val dollar = useCase().first()
        assertEquals("USD", dollar.currency)
        assertEquals(50L, dollar.totalSpentMinor)
        assertEquals(0L, dollar.totalBudgetMinor)
    }
}
