package com.falcon.ggsipunotices.ui

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.falcon.ggsipunotices.model.Notice
import com.google.accompanist.placeholder.material.placeholder
import java.io.File

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NoticeItem(notice: Notice = Notice(0, "14-09-2003", "Syllabus", "www.google.com", "never")) {
    val context = LocalContext.current
    val fileDownloaded = remember { mutableStateOf(false) }
    val fileTitle = notice.title.plus(".pdf") // For download / share purposes
    // Check if file is already downloaded
    LaunchedEffect(notice) {
        fileDownloaded.value = checkIfFileExists(context, notice.title)
    }

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
                    if (fileDownloaded.value) {
                        openFile(context, fileTitle)
                    } else {
                        downloadFile(context, notice.url, fileTitle, onSuccess = {
                            fileDownloaded.value = true
                            openFile(context, fileTitle)
                        })
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download & Open",
                        tint = Color.Blue
                    )
                }
                IconButton(onClick = {
                    if (fileDownloaded.value) {
                        shareFile(context, fileTitle)
                    } else {
                        downloadFile(context, notice.url, fileTitle, onSuccess = {
                            fileDownloaded.value = true
                            shareFile(context, fileTitle)
                        })
                    }
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

fun checkIfFileExists(context: Context, fileName: String): Boolean {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
    return file.exists()
}

fun downloadFile(
    context: Context,
    fileUrl: String,
    fileName: String,
    onSuccess: () -> Unit
) {
    val fileUrl = "https://github.com/labmember003/usar_data/raw/master/YEAR_1/Sem1/CommunicationSkills/Exam/MinorExam.pdf" //TODO: Remove this line
    val request = DownloadManager.Request(Uri.parse(fileUrl))
        .setTitle(fileName)
        .setDescription("Downloading $fileName")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) // Updated line

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = downloadManager.enqueue(request)

    // Check if file was downloaded successfully
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                onSuccess()
            } else {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
            }
        }
        cursor.close()
    }, 5000) // Delay to allow download to start
}


fun openFile(context: Context, fileName: String) {
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
    if (file.exists()) {
        val myIntent = Intent(Intent.ACTION_VIEW)
        val fileProviderUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        myIntent.data = fileProviderUri
        myIntent.setDataAndType(fileProviderUri, "application/pdf")
        myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val intent = Intent.createChooser(myIntent, "Choose an application to open with:")
        try {
            startActivity(context, intent,null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No application found to open this file", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(
            context,
            "File Exists But File Does Not Exist",
            Toast.LENGTH_LONG
        ).show()
    }
}

fun shareFile(context: Context, fileName: String) {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Share file"))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No application found to share this file", Toast.LENGTH_SHORT).show()
    }
}
