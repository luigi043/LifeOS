package com.luigi.lifeos.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(repository: UserProfileRepository) : ViewModel() {
    val state = repository.observe().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
