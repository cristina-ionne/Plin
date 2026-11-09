package com.plin.domain.enums

import androidx.compose.ui.graphics.Color
import com.plin.ui.theme.PlinColors

/**
 * Broad grouping for a task (work, personal, etc.).
 */
enum class TaskCategory {
    GENERIC,
    FUN,
    PERSONAL_DEVELOPMENT,
    SOCIAL_ACTIVITY,
    CHORE,
    ADULTING,
    SPORT,
    HEALTH,
    SPECIAL,
    FRIEND_OF_THE_WEEK;

    fun toDisplayString(): String {
        return this.name
            .lowercase()
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
    }

    val color: Color
        get() = when (this) {
            GENERIC -> PlinColors.PlinBlue
            FUN -> PlinColors.PlinPurple
            PERSONAL_DEVELOPMENT -> PlinColors.PlinGreen
            SOCIAL_ACTIVITY -> PlinColors.PlinTeal
            CHORE -> PlinColors.PlinBrown
            ADULTING -> PlinColors.PlinGrey
            SPORT -> PlinColors.PlinDarkBlue
            HEALTH -> PlinColors.PlinRed
            SPECIAL -> PlinColors.PlinFuchsia
            FRIEND_OF_THE_WEEK -> PlinColors.PlinYellow

            
        }
}
