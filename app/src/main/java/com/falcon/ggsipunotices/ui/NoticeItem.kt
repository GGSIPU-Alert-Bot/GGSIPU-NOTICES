package com.falcon.ggsipunotices.ui

import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.ggsipunotices.model.Notice
import com.google.accompanist.placeholder.material.placeholder
import java.io.File

@Composable
fun NoticeItem(
    notice: Notice = Notice(0, "14-09-2003", "Syllabus", "www.google.com", "never"),
    startDownloading: (String, String) -> Unit,
    openFile: (File) -> Unit,
    shareFile: (String) -> Unit
) {
    val fileTitle = notice.title.plus(".pdf") // For download / share purposes
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileTitle)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .placeholder(visible = false)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(notice.createdAt, color = Color.Gray)
            }
            Row {
                IconButton(onClick = {
                    if (!file.exists()) {
                        startDownloading(notice.url, fileTitle)
                    }
                    openFile(file)
                }) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download & Open",
                        tint = Color.Blue
                    )
                }
                IconButton(onClick = {
                    if (!file.exists()) {
                        startDownloading(notice.url, fileTitle)
                    }
                    shareFile(fileTitle)
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Blue
                    )
                }
            }
        }
    }
}