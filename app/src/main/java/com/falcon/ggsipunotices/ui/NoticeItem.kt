package com.falcon.ggsipunotices.ui

import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun NoticeItem(
    notice: Notice
) {
    val formatedDate = notice.date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it) }?.let { SimpleDateFormat("MMM-dd", Locale.getDefault()).format(it) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.white))
            .padding(8.dp, 8.dp, 8.dp, 0.dp)
            .clickable {
                // TODO: Open file
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically // TODO
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
