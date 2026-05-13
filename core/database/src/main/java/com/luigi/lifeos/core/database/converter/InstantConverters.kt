package com.luigi.lifeos.core.database.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

/**
 * Room can't natively persist java.time types. Centralising the converters
 * here means every entity stores timestamps the same way (epoch millis for
 * Instant, ISO-8601 string for LocalDate — readable in SQLite browsers).
 */
class InstantConverters {

    @TypeConverter
    fun instantToMillis(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun millisToInstant(millis: Long?): Instant? = millis?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun dateToString(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun stringToDate(s: String?): LocalDate? = s?.let(LocalDate::parse)
}
