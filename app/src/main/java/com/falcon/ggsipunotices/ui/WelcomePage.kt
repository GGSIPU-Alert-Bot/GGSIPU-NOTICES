package com.falcon.ggsipunotices.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.ggsipunotices.LottieAnimation
import com.falcon.ggsipunotices.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomePage(
//    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(30.dp))
        LottieAnimation(R.raw.welcome)
        Text(
            text = "WELCOME TO GGSIPU NOTICES",
            fontSize = 23.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .padding(8.dp, 0.dp, 8.dp, 0.dp)
        )
        Text(
            text = "Get all GGSIPU notices with notifications and easy sharing, all in one app!",
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.nunito_extralight)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.ExtraLight
            ),
            modifier = Modifier
                .padding(32.dp, 5.dp, 32.dp, 0.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(40.dp))
        FabHowToUsePage()
    }
}

@Composable
private fun FabHowToUsePage() {
    FloatingActionButton(
        onClick = {
            // TODO: Handle the click event
        },
        backgroundColor = Color.Black,
        contentColor = Color.White,
        modifier = Modifier
            .padding(4.dp)
            .size(56.dp),
        shape = RoundedCornerShape(percent = 30),
    ) {
        Icon(
            imageVector = Icons.Filled.NavigateNext,
            contentDescription = "Go",
            tint = colorResource(id = R.color.white),
        )
    }
}