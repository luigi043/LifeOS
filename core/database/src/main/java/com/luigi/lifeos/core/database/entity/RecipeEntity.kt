package com.luigi.lifeos.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "servings")
    val servings: Int = 1,

    @ColumnInfo(name = "prep_time_minutes")
    val prepTimeMinutes: Int = 0,

    @ColumnInfo(name = "cook_time_minutes")
    val cookTimeMinutes: Int = 0,

    @ColumnInfo(name = "notes")
    val notes: String = "",
)
