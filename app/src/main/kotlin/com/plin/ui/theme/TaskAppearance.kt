package com.plin.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared look for task rows and UI elements.
 */
object TaskAppearance {
    // Dimensions and shapes
    val cornerRadius: Dp = 30.dp
    
    // Colors & typography
    val borderColor = PlinDark.copy(alpha = 0.3f)
    val textColorPrimary = PlinDark
    val textColorSecondary = PlinDark.copy(alpha = 0.75f)
    
    // Font sizes (used in TaskItem)
    val titleFontSize = 16.sp
    val categoryFontSize = 12.sp
    val pointsFontSize = 12.sp
}