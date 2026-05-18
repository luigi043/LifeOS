package com.luigi.lifeos.core.data.repository

import com.luigi.lifeos.core.database.dao.UserProfileDao
import com.luigi.lifeos.core.database.entity.toDomain
import com.luigi.lifeos.core.database.entity.toEntity
import com.luigi.lifeos.core.domain.model.UserProfile
import com.luigi.lifeos.core.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed implementation. The repo *only* mediates the DAO + mappers —
 * onboarding completion is tracked in DataStore (UserPreferences.onboardingDone)
 * rather than here so the "should we show onboarding?" check stays fast
 * and doesn't have to round-trip through the DB on cold start.
 */
@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao,
) : UserProfileRepository {

    override fun observe(): Flow<UserProfile?> =
        dao.observe().map { it?.toDomain() }

    override suspend fun get(): UserProfile? =
        dao.get()?.toDomain()

    override suspend fun save(profile: UserProfile) {
        dao.upsert(profile.toEntity())
    }

    override suspend fun clear() {
        dao.clear()
    }
}
