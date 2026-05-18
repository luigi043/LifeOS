package com.luigi.lifeos.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luigi.lifeos.core.domain.model.ActivityLevel
import com.luigi.lifeos.core.domain.model.DietaryPreference
import com.luigi.lifeos.core.domain.model.FitnessGoal
import com.luigi.lifeos.core.domain.model.UserProfile

/**
 * Singleton-row table — only one user profile per device for now.
 * The id is pinned to [UserProfile.SINGLETON_ID] so an upsert always
 * targets the same row (no orphan rows from a botched insert).
 *
 * Enums are stored as strings (Room defaults), which keeps DB rows
 * readable in tools like Android Studio's Database Inspector at the
 * cost of one extra byte each. Worth it for debuggability.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = UserProfile.SINGLETON_ID,
    val displayName: String,
    val age: Int?,
    val heightCm: Double?,
    val weightKg: Double?,
    val goalWeightKg: Double?,
    val fitnessGoal: FitnessGoal,
    val activityLevel: ActivityLevel,
    val dietaryPreference: DietaryPreference,
    val monthlyIncomeMinor: Long?,
    val monthlyBudgetMinor: Long?,
    val foodBudgetMinor: Long?,
    val preferredCurrency: String,
    val createdAtEpochMillis: Long,
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    displayName = displayName,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    goalWeightKg = goalWeightKg,
    fitnessGoal = fitnessGoal,
    activityLevel = activityLevel,
    dietaryPreference = dietaryPreference,
    monthlyIncomeMinor = monthlyIncomeMinor,
    monthlyBudgetMinor = monthlyBudgetMinor,
    foodBudgetMinor = foodBudgetMinor,
    preferredCurrency = preferredCurrency,
    createdAtEpochMillis = createdAtEpochMillis,
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    displayName = displayName,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    goalWeightKg = goalWeightKg,
    fitnessGoal = fitnessGoal,
    activityLevel = activityLevel,
    dietaryPreference = dietaryPreference,
    monthlyIncomeMinor = monthlyIncomeMinor,
    monthlyBudgetMinor = monthlyBudgetMinor,
    foodBudgetMinor = foodBudgetMinor,
    preferredCurrency = preferredCurrency,
    createdAtEpochMillis = createdAtEpochMillis,
)
