package com.plin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import com.plin.ui.theme.FustatFamily
import com.plin.ui.theme.PlinColors
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.res.painterResource
import com.plin.R


//Today welcome card

@Composable
fun TodayCard(onSizeMeasured: (Int) -> Unit) {

    val today = LocalDate.now()
    val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val dayValue = today.dayOfWeek.value // 1 (Mon) to 7 (Sun)

    Box(
        modifier = Modifier
        .fillMaxWidth()
        .onSizeChanged { size -> onSizeMeasured(size.height)}
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(PlinColors.PlinBlue)
                .padding(24.dp)
        ) {
            Text(
                text = dayName,
                fontSize = 36.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PlinColors.PlinWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF0FAFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(dayValue / 7f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDD6900))
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Label "Day X of 7"
            Text(
                text = "Day $dayValue of 7",
                fontSize = 16.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.Normal,
                color = PlinColors.PlinWhite
            )
        }
    }
}


//Completed welcome card

@Composable
fun WellDoneCard(fixedHeight: Int? = null) {

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
                text = "Well done!",
                fontSize = 36.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.ExtraBold,
                color = PlinColors.PlinWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Subtitle
            Text(
                text = "You've reached so many goals.",
                fontSize = 18.sp,
                fontFamily = FustatFamily,
                fontWeight = FontWeight.Normal,
                color = PlinColors.PlinWhite,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }


//Friends welcome card

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



    //Rewards welcome card

    @Composable
fun RewardsCard(totalPoints: Int, fixedHeight: Int? = null) {

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
            lineHeight = 24.sp,
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
                .background(PlinColors.PlinWhite.copy(alpha = 0.6f))

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.ic_star), contentDescription = "Points")

                Text(
                    text = "$totalPoints",
                    color = PlinColors.PlinOrange,
                    fontSize = 24.sp,
                    fontFamily = FustatFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Text (
                    text = "points",
                    color = PlinColors.PlinOrange,
                    fontSize = 18.sp,
                    fontFamily = FustatFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

    }
}