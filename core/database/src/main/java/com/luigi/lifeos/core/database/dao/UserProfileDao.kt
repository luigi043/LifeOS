package com.luigi.lifeos.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.luigi.lifeos.core.database.entity.UserProfileEntity
import com.luigi.lifeos.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * DAO is intentionally narrow — UserProfile is a singleton row, so
 * "insertOrUpdate" semantics via @Upsert is enough. Surface a Flow for
 * reactive observers (the dashboard re-renders on profile changes).
 */
@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun observe(id: Long = UserProfile.SINGLETON_ID): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    suspend fun get(id: Long = UserProfile.SINGLETON_ID): UserProfileEntity?

    @Upsert
    suspend fun upsert(entity: UserProfileEntity): Long

    @Query("DELETE FROM user_profile")
    suspend fun clear(): Int
}
