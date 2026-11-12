package com.plin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.plin.ui.theme.PlinColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.plin.ui.theme.FustatFamily
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.plin.ui.theme.PlinWhite
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.plin.data.PlinDatabase
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plin.domain.models.Friend


@Composable
fun FriendsScreen() {

    val context = LocalContext.current
    val friendDao = remember { PlinDatabase.getInstance(context).friendDao() }
    
    val viewModel: FriendListViewModel = viewModel(
        factory = FriendListViewModel.FriendViewModelFactory(friendDao)
    )

    // Observe friends list in real time
    val friendsList by friendDao.getAllFriends().collectAsState(initial = emptyList())
    
    var showAddFriendOverlay by remember { mutableStateOf(false) }
    var friendToEdit by remember { mutableStateOf<Friend?>(null) }
    var showEditFriendOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 90.dp)
        ) {
            RelationshipCard()
            Spacer(modifier = Modifier.height(24.dp))

            // Friends list goes here
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(friendsList) { friend ->
                    FriendItem(
                        name = friend.name,
                        onLongClick = {
                            friendToEdit = friend
                            showEditFriendOverlay = true
                        }
                    )
                }
            }
        }


        // "+ Add friend" button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Button(
                onClick = { showAddFriendOverlay = true },
                colors = ButtonDefaults.buttonColors(containerColor = PlinColors.PlinOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+ Add Friend",
                    fontSize = 16.sp,
                    fontFamily = FustatFamily,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlinWhite
                )
            }
        }

        //Black background dimming with fade in/out animation
        AnimatedVisibility(
            visible = showAddFriendOverlay || friendToEdit != null,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        showAddFriendOverlay = false
                        friendToEdit = null
                    }
            )
        }

        //Add Friend Overlay with scale and fade in/out animation
        AnimatedVisibility(
            visible = showAddFriendOverlay,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
            ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
            exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AddFriendOverlay(
                    onDismiss = { showAddFriendOverlay = false },
                    onSave = { name, freq ->
                        viewModel.addFriend(name, freq)
                        showAddFriendOverlay = false
                    }
                )
            }
        }

        //Edit Task Overlay with scale and fade in/out animation
        AnimatedVisibility(
            visible = friendToEdit != null,
            enter = scaleIn(
                initialScale = 0.8f, 
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f)
            ) + fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
            exit = scaleOut(targetScale = 0.9f, animationSpec = spring()) + fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                friendToEdit?.let { friend ->
                    EditFriendOverlay(
                        friend = friend,
                        onDismiss = { friendToEdit = null },
                        onSave = { updated -> 
                            viewModel.updateFriend(updated)
                            friendToEdit = null
                        },
                        onDelete = {
                            viewModel.deleteFriend(friend)
                            friendToEdit = null
                        }
                    )
                }
            }
        }
    }
}


