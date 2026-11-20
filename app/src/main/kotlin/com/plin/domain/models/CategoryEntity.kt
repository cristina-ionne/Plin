package com.plin.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorArgb: Int // Room does not save the type Color, so we save the int (ARGB)
)