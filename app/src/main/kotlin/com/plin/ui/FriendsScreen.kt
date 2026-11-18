package com.plin.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plin.domain.models.Friend
import android.app.Application


@Composable
fun FriendsScreen(
    onAddFriend: () -> Unit,
    onEditFriend: (Friend) -> Unit
) {

    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: FriendListViewModel = viewModel(
        factory = FriendListViewModel.FriendViewModelFactory(application)
    )

    val friendsList by viewModel.friends.collectAsStateWithLifecycle()

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
                items(
                    items = friendsList,
                    key = { friend -> friend.id } //For the animation
                ) { friend ->
                    Box(modifier = Modifier.animateItem()) {
                        FriendItem(
                            name = friend.name,
                            onLongClick = {
                                onEditFriend(friend)
                            }
                        )
                    }
                }
            }
        }


        // "+ Add friend" button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = { onAddFriend() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
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
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}


