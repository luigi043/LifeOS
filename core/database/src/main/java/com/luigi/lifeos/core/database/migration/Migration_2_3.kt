package com.luigi.lifeos.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds the Nutrition + Grocery feature tables:
 *   ingredients, recipes, recipe_ingredients, grocery_items
 *
 * Column types follow SQLite affinity rules (same convention as MIGRATION_1_2):
 *   INTEGER → Long / Int / Boolean
 *   TEXT    → String / nullable String / enum names
 *   REAL    → Double / nullable Double
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // ── ingredients ──────────────────────────────────────────────────────
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `ingredients` (
                `id`                       INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name`                     TEXT    NOT NULL,
                `unit`                     TEXT    NOT NULL,
                `estimated_price_per_unit` REAL,
                `category`                 TEXT    NOT NULL DEFAULT 'OTHER'
            )
            """.trimIndent(),
        )

        // ── recipes ──────────────────────────────────────────────────────────
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recipes` (
                `id`                 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name`               TEXT    NOT NULL,
                `servings`           INTEGER NOT NULL DEFAULT 1,
                `prep_time_minutes`  INTEGER NOT NULL DEFAULT 0,
                `cook_time_minutes`  INTEGER NOT NULL DEFAULT 0,
                `notes`              TEXT    NOT NULL DEFAULT ''
            )
            """.trimIndent(),
        )

        // ── recipe_ingredients (join table) ───────────────────────────────────
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recipe_ingredients` (
                `recipe_id`     INTEGER NOT NULL,
                `ingredient_id` INTEGER NOT NULL,
                `quantity`      REAL    NOT NULL,
                `unit`          TEXT    NOT NULL,
                PRIMARY KEY (`recipe_id`, `ingredient_id`),
                FOREIGN KEY (`recipe_id`)
                    REFERENCES `recipes`(`id`)
                    ON DELETE CASCADE,
                FOREIGN KEY (`ingredient_id`)
                    REFERENCES `ingredients`(`id`)
                    ON DELETE CASCADE
            )
            """.trimIndent(),
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_recipe_id`
            ON `recipe_ingredients` (`recipe_id`)
            """.trimIndent(),
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_ingredient_id`
            ON `recipe_ingredients` (`ingredient_id`)
            """.trimIndent(),
        )

        // ── grocery_items ─────────────────────────────────────────────────────
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `grocery_items` (
                `id`                       INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name`                     TEXT    NOT NULL,
                `quantity`                 REAL    NOT NULL,
                `unit`                     TEXT    NOT NULL,
                `estimated_price_per_unit` REAL,
                `is_purchased`             INTEGER NOT NULL DEFAULT 0,
                `category`                 TEXT    NOT NULL DEFAULT 'OTHER',
                `source_recipe_id`         INTEGER
            )
            """.trimIndent(),
        )
    }
}
