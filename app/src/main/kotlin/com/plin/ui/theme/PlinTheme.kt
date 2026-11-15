package com.plin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme


private val DarkColors = darkColorScheme(
    primary = PlinColors.PlinBlueDark,
    onPrimary = PlinColors.PlinWhite,
    secondary = PlinColors.PlinOrange,
    onSecondary = PlinColors.PlinWhite,
    tertiary = PlinColors.PlinGrey,
    onTertiary = PlinColors.PlinRed,
    background = PlinColors.PlinDark,
    onBackground = PlinColors.PlinWhite,
    surface = PlinColors.PlinDark,
    onSurface = PlinColors.PlinWhite,
)


private val LightColors = lightColorScheme(
    primary = PlinColors.PlinBlue,
    onPrimary = PlinColors.PlinWhite,
    secondary = PlinColors.PlinOrange,
    onSecondary = PlinColors.PlinWhite,
    tertiary = PlinColors.PlinGrey,
    onTertiary = PlinColors.PlinRed,
    background = PlinColors.PlinWhite,
    onBackground = PlinColors.PlinDark,
    surface = PlinColors.PlinWhite,
    onSurface = PlinColors.PlinDark,
)

@Composable
fun PlinTheme(useDarkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
