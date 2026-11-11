package com.plin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plin.R
import com.plin.ui.theme.FustatFamily
import com.plin.ui.theme.PlinColors
import com.plin.ui.theme.TaskAppearance
import com.plin.ui.theme.TaskAppearance.titleFontSize

@Composable
fun FriendItem(
    name: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TaskAppearance.cornerRadius))
            .background(PlinColors.PlinBlue.copy(alpha=0.3f))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_single_friend),
                contentDescription = "Friend icon",
                modifier = Modifier
                    .size(22.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = name,
                    color = PlinColors.PlinDark,
                    fontSize = titleFontSize,
                    fontFamily = FustatFamily
                )
            }
        }
    }
}