package com.plin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.plin.ui.PlinApp
import com.plin.ui.theme.PlinTheme
import androidx.core.view.WindowCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import com.plin.data.ThemePreferences


/**
 * App entry activity. Hosts the Compose UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("PLIN", "MainActivity started")
        enableEdgeToEdge()

        val themePrefs = ThemePreferences(applicationContext)

        setContent {

            val systemInDark = isSystemInDarkTheme()

            var isDarkTheme by remember {
                mutableStateOf(
                    if (themePrefs.hasPreference()) themePrefs.isDarkMode()
                    else systemInDark
                )
            }

            val view = LocalView.current

            SideEffect {
                val window = (view.context as android.app.Activity).window
                val insetsController = WindowCompat.getInsetsController(window, view)

                // isAppearanceLightStatusBars = true -> dark icons (for light theme)
                // isAppearanceLightStatusBars = false -> light icons (for dark theme)
                insetsController.isAppearanceLightStatusBars = !isDarkTheme

                // Optional if you have a navigation bar
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme
            }

            PlinTheme(useDarkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlinApp(
                        currentDarkTheme = isDarkTheme,
                        onThemeChanged = { newTheme ->
                            isDarkTheme = newTheme
                            themePrefs.setDarkMode(newTheme)
                        }
                    )
                }
            }
        }
    }
}
