package com.plin.ui

import FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsOverlay(
    onDismiss: () -> Unit,
    onNavigateToCategories: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FAFF))
            .padding(22.dp, 28.dp, 22.dp, 22.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Settings",
            modifier = Modifier.fillMaxWidth().height(51.dp),
            color = Color(0xFF193F49),
            fontFamily = FustatFamily,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(45.dp))

        // Account section (placeholder for future settings)
        SettingsSection("Account") {
        }

        Spacer(modifier = Modifier.height(15.dp))

        //Categories section
        SettingsSection("Categories", onClick = onNavigateToCategories)
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
                color = Color(0xFF193F49),
                fontWeight = FontWeight.SemiBold
            )
      }
}

@Composable
fun SettingsOverlay(onDismiss: () -> Unit) {
   }