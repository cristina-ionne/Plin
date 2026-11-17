package com.plin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plin.domain.models.Friend
import com.plin.ui.theme.FustatFamily

/**
 * Linked-friends control for Social Activity tasks.
 * Opens an ExposedDropdownMenu checklist (same pattern as duration).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkedFriendsPicker(
    friends: List<Friend>,
    selectedFriendIds: Set<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onToggleFriend: (String) -> Unit,
) {
    val selectedFriends = friends.filter { it.id in selectedFriendIds }
    val summaryText = when {
        selectedFriends.isEmpty() -> "No linked friends"
        else -> selectedFriends.joinToString(separator = ", ") { it.name }
    }

    Text(
        text = "Link a friend to this task",
        fontSize = 14.sp,
        fontFamily = FustatFamily,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        modifier = Modifier.padding(bottom = 4.dp),
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    RoundedCornerShape(10.dp),
                )
                .background(MaterialTheme.colorScheme.surface)
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = summaryText,
                fontSize = 16.sp,
                fontFamily = FustatFamily,
                color = if (selectedFriends.isEmpty()) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                },
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp),
                )
                .heightIn(max = 205.dp),
        ) {
            if (friends.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No friends in your pool yet",
                            fontFamily = FustatFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    },
                    onClick = { onExpandedChange(false) },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                )
            } else {
                friends.forEachIndexed { index, friend ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = friend.id in selectedFriendIds,
                                    onCheckedChange = { onToggleFriend(friend.id) },
                                )
                                Text(
                                    text = friend.name,
                                    fontSize = 16.sp,
                                    fontFamily = FustatFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        },
                        onClick = { onToggleFriend(friend.id) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        ),
                    )
                    if (index != friends.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}
