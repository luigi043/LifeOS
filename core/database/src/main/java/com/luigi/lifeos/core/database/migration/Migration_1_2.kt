package com.luigi.lifeos.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds the Finance feature tables: expense_categories, expenses, income, budgets.
 *
 * Column types follow SQLite affinity rules:
 *   INTEGER → Long / Int / Boolean (Room stores booleans as 0/1)
 *   TEXT    → String / nullable String / enum names
 *   REAL    → Double (none here yet)
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `expense_categories` (
                `id`         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name`       TEXT    NOT NULL,
                `icon_name`  TEXT    NOT NULL,
                `color_hex`  TEXT    NOT NULL,
                `is_default` INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `expenses` (
                `id`                      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `amount_minor`            INTEGER NOT NULL,
                `currency`                TEXT    NOT NULL,
                `category_id`             INTEGER NOT NULL,
                `note`                    TEXT    NOT NULL,
                `is_recurring`            INTEGER NOT NULL,
                `recurring_period`        TEXT,
                `date_epoch_millis`       INTEGER NOT NULL,
                `created_at_epoch_millis` INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_expenses_date`
            ON `expenses` (`date_epoch_millis`)
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `income` (
                `id`                      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `amount_minor`            INTEGER NOT NULL,
                `currency`                TEXT    NOT NULL,
                `source`                  TEXT    NOT NULL,
                `is_recurring`            INTEGER NOT NULL,
                `recurring_period`        TEXT,
                `date_epoch_millis`       INTEGER NOT NULL,
                `created_at_epoch_millis` INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_income_date`
            ON `income` (`date_epoch_millis`)
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `budgets` (
                `id`           INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `category_id`  INTEGER,
                `limit_minor`  INTEGER NOT NULL,
                `currency`     TEXT    NOT NULL,
                `period_start` INTEGER NOT NULL,
                `period_end`   INTEGER NOT NULL
            )
        """.trimIndent())
    }
}
