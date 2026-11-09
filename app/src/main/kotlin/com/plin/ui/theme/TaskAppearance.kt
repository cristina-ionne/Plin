package com.plin.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plin.domain.enums.TaskCategory

/**
 * Shared look for task rows: rounded rectangle container.
 * Color comes from [colorForCategory]; shape/padding are fixed here.
 */
object TaskAppearance {
    val cornerRadius: Dp = 30.dp
    val borderWidth: Dp = 1.dp
    val contentPaddingHorizontal: Dp = 14.dp
    val contentPaddingVertical: Dp = 12.dp
    val shape = RoundedCornerShape(cornerRadius)
    val borderColor = PlinDark.copy(alpha = 0.3f)
    val textColorPrimary = PlinDark
    val textColorSecondary = PlinDark.copy(alpha = 0.75f)

    fun colorForCategory(category: TaskCategory): Color = when (category) {
        TaskCategory.GENERIC -> PlinColors.PlinBlue
        TaskCategory.FUN -> PlinColors.PlinPurple
        TaskCategory.PERSONAL_DEVELOPMENT -> PlinColors.PlinGreen
        TaskCategory.SOCIAL_ACTIVITY -> PlinColors.PlinTeal
        TaskCategory.CHORE -> PlinColors.PlinBrown
        TaskCategory.ADULTING -> PlinColors.PlinGrey
        TaskCategory.SPORT -> PlinColors.PlinDarkBlue
        TaskCategory.HEALTH -> PlinColors.PlinRed
        TaskCategory.SPECIAL -> PlinFuchsia
        TaskCategory.FRIEND_OF_THE_WEEK -> PlinColors.PlinYellow
    }

    fun backgroundForCategory(category: TaskCategory): Color =
        colorForCategory(category).copy(alpha = 0.12f)
}
