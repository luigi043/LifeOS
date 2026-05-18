package com.luigi.lifeos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Resolves whether onboarding has run before, then keeps that decision
 * pinned for the activity lifetime. We deliberately do NOT subscribe to
 * the [UserPreferences.onboardingDone] flow at the activity level — once
 * the user finishes onboarding mid-session, the nav graph already pops
 * onboarding off (see LifeOsNavHost.onboardingScreen.onFinished), so
 * re-routing the start destination on the fly would just cause flicker.
 */
@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _entryState = MutableStateFlow<EntryState>(EntryState.Loading)
    val entryState: StateFlow<EntryState> = _entryState.asStateFlow()

    init {
        viewModelScope.launch {
            val done = userPreferences.onboardingDone.first()
            _entryState.value = EntryState.Ready(onboardingDone = done)
        }
    }
}

sealed interface EntryState {
    data object Loading : EntryState
    data class Ready(val onboardingDone: Boolean) : EntryState
}
