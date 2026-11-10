package com.plin.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.plin.ui.theme.FustatFamily
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.plin.ui.theme.PlinWhite


@Composable
fun FriendsScreen() {

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
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                //Friends
            }
        }


        // "+ Add friend" button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Button(
                onClick = { /*add friend logic */ },
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
    }
}



    @Composable
    fun RelationshipCard() {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(PlinColors.PlinOrange)
                .padding(top = 20.dp, start= 24.dp, end = 24.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Relationships are important!",
                fontSize = 36.sp,
                lineHeight = 36.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PlinColors.PlinWhite
            )


            //Subtitle
            Text(
                text = "Don't forget to take good care of them.",
                fontSize = 18.sp,
                lineHeight = 18.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.Normal,
                color = PlinColors.PlinWhite,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }

