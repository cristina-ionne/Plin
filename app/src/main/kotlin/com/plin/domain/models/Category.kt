package com.plin.domain.models

import androidx.compose.ui.graphics.Color

data class Category(
    val id: String,
    val name: String,
    val color: Color,
    val isSystem: Boolean = false // To distinguish system enums from customs categories
)