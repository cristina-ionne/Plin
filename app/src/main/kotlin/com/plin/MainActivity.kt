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
import androidx.compose.ui.platform.LocalView


/**
 * App entry activity. Hosts the Compose UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("PLIN", "MainActivity started")
        enableEdgeToEdge()
        setContent {

            val isDarkTheme = isSystemInDarkTheme()
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
                    PlinApp()
                }
            }
        }
    }
}
