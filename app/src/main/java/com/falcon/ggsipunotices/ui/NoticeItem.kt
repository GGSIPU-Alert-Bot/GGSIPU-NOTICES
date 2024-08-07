package com.falcon.ggsipunotices.ui

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.falcon.ggsipunotices.R
import com.falcon.ggsipunotices.model.Notice
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoticeItemPreview() {
    NoticeItem(
        notice = Notice(0, "14-09-2003", "Syllabus", "www.google.com", "never")
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun NoticeItem(
    notice: Notice
) {
    val formatedDate = try {
        notice.date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it) }?.let { SimpleDateFormat("MMM-dd", Locale.getDefault()).format(it) }
    } catch (e: Exception) {
        Log.i("Error NoticeItem:", e.message.toString())
        null
    }
    var isVisible by remember { mutableStateOf(true) } // TODO: Change According to If the Notice is New
    val borderAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    LaunchedEffect(Unit) {
        delay(2000)
        isVisible = false
    }

    BoxWithConstraints(
        modifier = Modifier
            .padding(8.dp)
            .drawBehind {
                if (borderAlpha > 0) {
                    val stroke = Stroke(width = 2.dp.toPx())
                    val outline = Outline.Rectangle(Rect(radius = 10f, center = Offset(size.width, size.height)))
                    drawOutline(
                        outline = outline,
                        color = Color.Blue.copy(alpha = borderAlpha),
                        style = stroke
                    )
                }
            },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.white))
                    .padding(8.dp, 8.dp, 8.dp, 0.dp)
                    .clickable {
                        // TODO: Open file
                    },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically, // TODO
            ) {
                if (notice.iconURL != null) {
                    AsyncImage(
                        model = notice.iconURL,
                        contentDescription = "Subject picture",
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.notes_blue),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CutCornerShape(CornerSize(3.dp))),
                        contentScale = ContentScale.FillBounds
//                    .background(Color.Gray)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    androidx.compose.material.Text(
                        text = notice.title ?: "UNKNOWN TITLE",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_bold_1)),
                    )
                    Log.i("NoticeItem - date", "NoticeItem: ${notice.date}")
                    Log.i("NoticeItem - created At", "NoticeItem: ${notice.createdAt}")
                    androidx.compose.material.Text(
                        text = formatedDate ?: notice.date ?: notice.createdAt ?: "UNKNOWN DATE",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.nunito_light_1)),
                    )
                }

            }
        }
    )

}


//    Card(
//        shape = RoundedCornerShape(8.dp),
//        colors = CardDefaults.cardColors (
//            containerColor = Color.White
//        ),
//        border = BorderStroke(1.dp, Color.Black),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .placeholder(visible = false)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(8.dp)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.notes_blue),
//                contentDescription = "",
//                modifier = Modifier
//                    .size(28.dp)
//            )
//
//
//
//            Column(modifier = Modifier.weight(1f)) {
//                Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
//                Text(notice.createdAt, color = Color.Gray)
//            }
//            Row {
//                IconButton(onClick = {
//                    if (!file.exists()) {
//                        startDownloading(notice.url, fileTitle)
//                    }
//                    openFile(file)
//                }) {
//                    Icon(
//                        imageVector = Icons.Default.Download,
//                        contentDescription = "Download & Open",
//                        tint = Color.Blue
//                    )
//                }
//                IconButton(onClick = {
//                    if (!file.exists()) {
//                        startDownloading(notice.url, fileTitle)
//                    }
//                    shareFile(fileTitle)
//                }) {
//                    Icon(
//                        imageVector = Icons.Default.Share,
//                        contentDescription = "Share",
//                        tint = Color.Blue
//                    )
//                }
//            }
//        }
//    }





//@Composable
//fun NoticeItem2(
//    notice: Notice = Notice(0, "14-09-2003", "Syllabus", "www.google.com", "never"),
//    startDownloading: (String, String) -> Unit,
//    openFile: (File) -> Unit,
//    shareFile: (String) -> Unit
//) {
//    ListItem(
//        modifier = Modifier.clip(MaterialTheme.shapes.small),
//        headlineContent = {
//            Text(
//                notice.title,
//                style = MaterialTheme.typography.titleMedium
//            )
//        },
//        supportingContent = {
//            Text(
//                notice.createdAt,
//                style = MaterialTheme.typography.bodySmall
//            )
//        },
//        leadingContent = {
//            Icon(
//                Icons.Filled.Person,
//                contentDescription = "person icon",
//                Modifier
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.primaryContainer)
//                    .padding(10.dp)
//            )
//        }
//    )
//}
