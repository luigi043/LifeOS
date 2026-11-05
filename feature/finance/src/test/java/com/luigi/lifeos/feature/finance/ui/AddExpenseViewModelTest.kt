package com.luigi.lifeos.feature.finance.ui

import com.luigi.lifeos.core.datastore.UserPreferences
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import com.luigi.lifeos.feature.finance.domain.usecase.AddExpenseUseCase
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.Before
import org.junit.After

@OptIn(ExperimentalCoroutinesApi::class)
class AddExpenseViewModelTest {
    @Before fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }
    @After fun tearDown() { Dispatchers.resetMain() }
    @Test fun savesExactCentsInSelectedCurrencyAndRejectsDuplicateSave() = runTest {
            val repository = mockk<FinanceRepository>()
            val preferences = mockk<UserPreferences>()
            every { repository.observeCategories() } returns flowOf(listOf(ExpenseCategory(1, "Food")))
            every { preferences.baseCurrency } returns flowOf("USD")
            coEvery { repository.upsertExpense(any()) } coAnswers { delay(100); 1L }
            val vm = AddExpenseViewModel(AddExpenseUseCase(repository), repository, preferences)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
            runCurrent()
            vm.onAmountChanged("0,29")
            runCurrent()
            vm.onSave(); vm.onSave()
            advanceUntilIdle()
            coVerify(exactly = 1) { repository.upsertExpense(match { it.amountMinor == 29L && it.currency == "USD" && it.categoryId == 1L }) }
            assertFalse(vm.state.value.isSaving)
    }

    @Test fun failedSaveRetainsFormAndAllowsRetry() = runTest {
            val repository = mockk<FinanceRepository>()
            val preferences = mockk<UserPreferences>()
            every { repository.observeCategories() } returns flowOf(listOf(ExpenseCategory(1, "Food")))
            every { preferences.baseCurrency } returns flowOf("EUR")
            coEvery { repository.upsertExpense(any()) } throws java.io.IOException("synthetic failure")
            val vm = AddExpenseViewModel(AddExpenseUseCase(repository), repository, preferences)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.collect() }
            runCurrent()
            vm.onAmountChanged("12.34"); runCurrent(); vm.onSave(); advanceUntilIdle()
            assertFalse(vm.state.value.isSaving)
            assertNotNull(vm.state.value.error)
            assertEquals("12.34", vm.state.value.amountText)
            coEvery { repository.upsertExpense(any()) } returns 1L
            vm.onSave(); advanceUntilIdle()
            assertNull(vm.state.value.error)
            coVerify(exactly = 2) { repository.upsertExpense(any()) }
    }
}
