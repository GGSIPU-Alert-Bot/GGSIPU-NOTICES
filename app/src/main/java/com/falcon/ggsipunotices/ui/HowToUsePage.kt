package com.falcon.ggsipunotices.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.ggsipunotices.R
import kotlinx.coroutines.launch

@Composable
fun HowToUseAppPage(navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        HeadingSummarizedPage()
        SwipeLeftToOpen()
        Spacer(modifier = Modifier.height(10.dp))
        SwipeRightToShare()
        Spacer(modifier = Modifier.height(30.dp))
        FabHowToUsePage(navController)
    }

}

@Composable
fun HeadingSummarizedPage() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp, 8.dp, 12.dp)
    ) {
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
        )
        Text(
            text = "How To Use",
            fontSize = 23.sp,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .padding(8.dp, 0.dp, 8.dp, 0.dp)
        )
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun FabHowToUsePage(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    FloatingActionButton(
        onClick = {
            scope.launch {
                navController.navigate("main_screen")
            }
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

@Composable
private fun SwipeLeftToOpen() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.blue_arrow),
            contentDescription = "",
            modifier = Modifier
                .rotate(180f)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            "SWIPE LEFT TO OPEN",
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Image(
            painter = painterResource(id = R.drawable.blue_arrow),
            contentDescription = "",
            modifier = Modifier
                .rotate(180f)
        )
    }
    Card(
        modifier = Modifier
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp), // Adjust the corner radius as needed
    ) {
        Image(
            painter = painterResource(id = R.drawable.open_demo),
            contentDescription = ""
        )
    }
}

@Composable
private fun SwipeRightToShare() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.blue_arrow),
            contentDescription = "",
            modifier = Modifier
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            "SWIPE RIGHT TO SHARE",
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Image(
            painter = painterResource(id = R.drawable.blue_arrow),
            contentDescription = "",
            modifier = Modifier
        )
    }
    Card(
        modifier = Modifier
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp), // Adjust the corner radius as needed
    ) {
        Image(
            painter = painterResource(id = R.drawable.share_demo),
            contentDescription = ""
        )
    }
}