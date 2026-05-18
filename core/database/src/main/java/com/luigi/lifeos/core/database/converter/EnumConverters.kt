package com.luigi.lifeos.core.database.converter

import androidx.room.TypeConverter
import com.luigi.lifeos.core.domain.model.ActivityLevel
import com.luigi.lifeos.core.domain.model.DietaryPreference
import com.luigi.lifeos.core.domain.model.FitnessGoal
import com.luigi.lifeos.core.domain.model.RecurringPeriod

/**
 * Room can use enum names for free, but going through explicit converters
 * means renaming an enum value in code does NOT silently corrupt rows —
 * adding a value to the converter forces you to write a migration.
 */
class EnumConverters {

    @TypeConverter
    fun fitnessGoalToString(g: FitnessGoal): String = g.name

    @TypeConverter
    fun stringToFitnessGoal(s: String): FitnessGoal =
        runCatching { FitnessGoal.valueOf(s) }.getOrDefault(FitnessGoal.GENERAL_HEALTH)

    @TypeConverter
    fun activityLevelToString(a: ActivityLevel): String = a.name

    @TypeConverter
    fun stringToActivityLevel(s: String): ActivityLevel =
        runCatching { ActivityLevel.valueOf(s) }.getOrDefault(ActivityLevel.MODERATE)

    @TypeConverter
    fun dietaryPreferenceToString(d: DietaryPreference): String = d.name

    @TypeConverter
    fun stringToDietaryPreference(s: String): DietaryPreference =
        runCatching { DietaryPreference.valueOf(s) }.getOrDefault(DietaryPreference.OMNIVORE)

    // ── Finance enums ────────────────────────────────────────────────────────

    @TypeConverter
    fun recurringPeriodToString(p: RecurringPeriod?): String? = p?.name

    @TypeConverter
    fun stringToRecurringPeriod(s: String?): RecurringPeriod? =
        s?.let { runCatching { RecurringPeriod.valueOf(it) }.getOrNull() }
}
