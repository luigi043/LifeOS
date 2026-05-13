package com.luigi.lifeos.core.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.luigi.lifeos.core.database.converter.InstantConverters

/**
 * Unified Room database for LifeOS.
 *
 * Entities are added incrementally as features get ported. Each feature
 * module contributes its entities/DAOs — they're listed here because Room
 * needs the full entity set at compile time.
 *
 * MIGRATION POLICY: every schema change MUST ship a [androidx.room.migration.Migration].
 * NO `fallbackToDestructiveMigration()` in production. Schemas are exported to
 * `core/database/schemas/` and committed to git so Room can auto-validate
 * migrations in tests.
 *
 * [SchemaSentinelEntity] exists only so Room has at least one entity to
 * compile against during scaffolding. Replace with real entities (or just
 * delete it) once the first feature ports its tables.
 */
@Database(
    entities = [SchemaSentinelEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(InstantConverters::class)
abstract class LifeOsDatabase : RoomDatabase()

@Entity(tableName = "_schema_sentinel")
internal data class SchemaSentinelEntity(
    @PrimaryKey val id: Int = 0,
)
