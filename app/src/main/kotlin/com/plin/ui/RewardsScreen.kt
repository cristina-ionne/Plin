package com.plin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import FustatFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.plin.R
import com.plin.ui.theme.PlinBlue
import com.plin.ui.theme.PlinWhite


@Composable
fun RewardsScreen(viewModel: TaskListViewModel = viewModel()) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 90.dp)
        ) {
            RewardsCard()
            Spacer(modifier = Modifier.height(24.dp))

            // Rewards list goes here
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                //Rewards
            }
        }
    }
}


@Composable
fun RewardsCard(fixedHeight: Int? = null) {

    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (fixedHeight != null) Modifier.height(with(density) { fixedHeight.toDp() }) else Modifier)
            .clip(RoundedCornerShape(24.dp))
            .background(PlinColors.PlinBlue)
            .padding(24.dp)
    ) {
        Text(
            text = "Time to celebrate!",
            fontSize = 36.sp,
            lineHeight = 36.sp,
            fontFamily = FustatFamily,
            fontWeight = FontWeight.ExtraBold,
            color = PlinColors.PlinWhite
        )


        //Subtitle
        Text(
            text = "Use your points to claim your rewards.",
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontFamily = FustatFamily,
            fontWeight = FontWeight.Normal,
            color = PlinColors.PlinWhite

        )

        //Points
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(PlinColors.PlinWhite)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.ic_star), contentDescription = "Points")
            }
        }

    }
}
