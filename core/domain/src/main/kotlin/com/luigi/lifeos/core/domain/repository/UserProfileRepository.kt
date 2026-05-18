package com.luigi.lifeos.core.domain.repository

import com.luigi.lifeos.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for the device-local user profile.
 *
 * Interface lives in :core:domain (no Android dependencies) so feature
 * modules can declare-and-inject without pulling Room/DataStore into
 * their build graph. The Room-backed implementation lives in :core:data.
 */
interface UserProfileRepository {

    /** Observe the user profile. Emits `null` until onboarding finishes. */
    fun observe(): Flow<UserProfile?>

    /** One-shot read. Returns null if onboarding hasn't completed. */
    suspend fun get(): UserProfile?

    /** Insert-or-replace the singleton profile row. */
    suspend fun save(profile: UserProfile)

    /** Wipe the profile (used by Settings → Reset and tests). */
    suspend fun clear()
}
