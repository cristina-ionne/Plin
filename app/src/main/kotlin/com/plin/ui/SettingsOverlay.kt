package com.plin.ui

import com.plin.ui.theme.FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsOverlay(isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onNavigateToCategories: () -> Unit,)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(22.dp, 28.dp, 22.dp, 22.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            modifier = Modifier.fillMaxWidth().height(51.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FustatFamily,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(45.dp))

        // Account section (placeholder for future settings)
        SettingsSection("Account") {
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Categories section
        SettingsSection("Categories", onClick = onNavigateToCategories)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dark Mode",
                fontFamily = FustatFamily,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onToggleTheme(it) },
                colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, onClick: (() -> Unit)? = null) {
    TextButton(
        onClick = { onClick?.invoke() },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.wrapContentHeight()
    ) {
            Text(
                text = title,
                fontFamily = FustatFamily,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
      }
}
