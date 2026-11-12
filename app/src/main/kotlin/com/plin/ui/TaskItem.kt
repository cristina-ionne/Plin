package com.plin.ui

import com.plin.ui.theme.FustatFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plin.domain.models.Task
import com.plin.ui.theme.TaskAppearance
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import com.plin.R
import androidx.compose.foundation.shape.RoundedCornerShape
import com.plin.ui.theme.PlinWhite
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import com.plin.domain.enums.TaskCategory
import com.plin.ui.theme.PlinOrange
import kotlin.math.hypot

fun formatCategory(category: TaskCategory): String =
    category.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    task: Task,
    onCompletedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    
    // 1. Progressive filling animation (from 0f to 1f)
    val fillProgress by animateFloatAsState(
        targetValue = if (task.completed) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "liquid_fill_progress"
    )

    // 2. Text color animation to guarantee maximum readability
    val titleTextColor by animateColorAsState(
        targetValue = if (task.completed) PlinWhite else TaskAppearance.textColorPrimary,
        animationSpec = tween(durationMillis = 300),
        label = "title_color"
    )
    val subTextColor by animateColorAsState(
        targetValue = if (task.completed) PlinWhite.copy(alpha = 0.8f) else TaskAppearance.textColorSecondary,
        animationSpec = tween(durationMillis = 300),
        label = "sub_text_color"
    )
    val categoryTextColor by animateColorAsState(
    targetValue = if (task.completed) PlinWhite else task.category.color,
    animationSpec = tween(durationMillis = 300),
    label = "category_color"
)

    // Main card container with the circular wave animation and the click/long-click handlers
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TaskAppearance.cornerRadius))
            .background(PlinWhite)
            .border(0.dp, TaskAppearance.borderColor, RoundedCornerShape(TaskAppearance.cornerRadius))
            .drawWithContent {
                // A) Draw the default content (background, border, texts, icons) first
                drawContent()

                // B) If task is completed, draw the circular wave animation on top of the content
                if (fillProgress > 0f) {
                    // Checkbox center coordinates (14dp padding + 11dp half dimension of 22dp = 25dp)
                    val originX = 25.dp.toPx()
                    val originY = size.height / 2f

                    // Calculate the maximum radius needed to cover the whole card
                    val maxRadius = hypot(
                        maxOf(originX, size.width - originX),
                        maxOf(originY, size.height - originY)
                    )
                    val currentRadius = maxRadius * fillProgress

                    drawCircle(
                        color = PlinOrange,
                        radius = currentRadius,
                        center = Offset(originX, originY)
                    )

                    // Redraw the content over the orange wave (text and icons)
                    drawContent()
                }
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3. Checkbox animation: Scale-out for the empty circle and Scale-in for the ic_completed_white icon
            AnimatedContent(
                targetState = task.completed,
                label = "checkbox_animation"
            ) { isCompleted ->
                if (isCompleted) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_completed_white),
                        contentDescription = "Task completed",
                        modifier = Modifier
                            .size(22.dp)
                            .animateEnterExit(
                                enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                                //exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(PlinWhite, CircleShape)
                            .border(1.dp, TaskAppearance.borderColor, CircleShape)
                            .clickable { onCompletedChange(!task.completed) }
                            .animateEnterExit(
                                enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                                //exit = scaleOut(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Empty space for checkbox
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 4. Text, title and category
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = TaskAppearance.titleFontSize,
                    fontFamily = FustatFamily,
                    color = titleTextColor
                )
                Text(
                    text = formatCategory(task.category),
                    fontSize = TaskAppearance.categoryFontSize,
                    fontFamily = FustatFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = categoryTextColor,
                )
            }

            // 5. Points
            Text(
                text = "${task.points} pts",
                fontSize = TaskAppearance.pointsFontSize,
                fontFamily = FustatFamily,
                color = subTextColor
            )
        }
    }
}