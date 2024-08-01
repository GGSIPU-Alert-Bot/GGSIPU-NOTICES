package com.falcon.ggsipunotices

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import com.falcon.ggsipunotices.ui.NoticeListScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge() // Removed in order to bring status bar
        setContent {
            NoticeListScreen(
                startDownloading = ::downloadPdfNotification,
                openFile = ::openFile,
                shareFile = ::shareFile,
                activity = this
            )
        }
    }

    private fun openFile(context: Context, file: File) {
        if (file.exists()) {
            var attachmentUri = FileProvider.getUriForFile(
                this,
                this.application.packageName +".provider",
                file
            )
            if (ContentResolver.SCHEME_FILE == attachmentUri.scheme) { // Checks if attachmentUri is file URI or content URI
                // We need to give content URI to Intent always, because:
                // Direct access to file URIs is restricted in modern Android versions due to security reasons. Apps are encouraged to use content URIs instead.
                Log.i("3.5- openDownloadedAttachment1, attachmentUri:", attachmentUri.toString())
                val file = File(attachmentUri.path)
                attachmentUri =
                    FileProvider.getUriForFile(this, this.application.packageName +".provider", file)
            }
            Log.i("4- openDownloadedAttachment1, attachmentUri:", attachmentUri?.toString() ?: "null")
            val openAttachmentIntent = Intent(Intent.ACTION_VIEW)
            openAttachmentIntent.setDataAndType(attachmentUri, "application/pdf")
            openAttachmentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            openAttachmentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            openAttachmentIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            try {
                context.startActivity(openAttachmentIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No app to open this file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareFile(fileName: String, file: File) {
        val attachmentUri = FileProvider.getUriForFile(
            this,
            this.application.packageName +".provider",
            file
        )
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/pdf"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_STREAM, attachmentUri)
            this.startActivity(Intent.createChooser(intent, "Share File"))
        } else {
            Toast.makeText(this, "First Download the file", Toast.LENGTH_SHORT).show()
        }
    }
}



fun downloadPdfNotification(
    title: String,
    context: Context,
    pdfUrl: String?,
    notificationId: Int,
    scope: CoroutineScope,
    activity: ComponentActivity?,
) {
    Log.i("DPN", pdfUrl.toString())
//    val pdfUrl = "https://github.com/labmember003/usar_data/raw/master/YEAR_1/Sem1/EngineeringMechanics/paper/MinorExam.pdf" // TODO: Remove This Line
    if (pdfUrl == null) {
        Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
        return
    }



    scope.launch {
        withContext(Dispatchers.IO) {
            val notificationManager = NotificationManagerCompat.from(context)
            createNotificationChannel(context)

            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(pdfUrl).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body.let { body ->
                        val inputStream = body.byteStream()
                        val pdfBuffer = inputStream.readBytes()
                        savePdfBuffer(context, title, pdfBuffer)
                        notificationManager.cancel(notificationId)
                    }
                } else {
                    Toast.makeText(context, "1:"+response.message, Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "2:"+response.toString(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                    notificationManager.cancel(notificationId)
                }
            } catch (e: Exception) {
                Log.e("DPN - 1", e.toString())
                e.printStackTrace()
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                notificationManager.cancel(notificationId)
            }
        }
    }
}

private fun savePdfBuffer(context: Context, title: String, pdfBuffer: ByteArray) {
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, title)
    try {
        FileOutputStream(pdfFile).use { fos ->
            fos.write(pdfBuffer)
        }
        Log.i("PDF_SAVE", "PDF saved to ${pdfFile.absolutePath}")
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("PDF_SAVE", "Failed to save PDF")
    }
}


@SuppressLint("CoroutineCreationDuringComposition", "Range")
fun downloadPdfNotifination2(
    title: String,
    context: Context,
    pdfUrl: String?,
    notificationId: Int,
    scope: CoroutineScope,
    activity: ComponentActivity?,
) {
    Log.i("DPN", pdfUrl.toString())

//    val pdfUrl = "https://github.com/labmember003/usar_data/raw/master/YEAR_1/Sem1/EngineeringMechanics/paper/MinorExam.pdf" // TODO: Remove This Line
//    val pdfUrl = "https://maven.apache.org/archives/maven-1.x/maven.pdf"
//    val pdfUrl = "https://doc-04-3s-prod-02-apps-viewer.googleusercontent.com/viewer2/prod-02/pdf/tijr3fkphpaituvt589k0jte2n1tuqg7/uv6sel00v99kk0ul6126e4upnvh277e1/1722534075000/3/*/APznzaZgS90a_sWqqp4mjI2dF6OvnlFLgOle3IqLpiJn0rI-Pw1iWTakJKz2TFJKqe95poc63zhwgSf4PGwzYj3zLbIINfjdwMoTAdhWrtM9GlGAbE2PYFAHL35dIm20zCDHqgp8UO2ANJebjJLoHRmXmY-Vifgy8kThILyD6UyN3DhtSmDqsYS_Exkgxx5vrkqvl4hi9AoENX2r5HGudlj_wPte-Gt9WH6fk9c4OhZ9hmW6VqYHk4VxA5QVD5iYtBNaHoSIRb4ynzqv0Pg0voV6vEV89HoaJySOrlrY8j91IS3iZzicr5zpamrhdMPB1UrRzGbK-2cpsJvA7U8AA3k7qvr9z8APTxd8dUpimAoLW2Zdy_P_BWkkhJtEv2x_pLmSr5O7jfLenXcaF8Lf9YyBDy2rzxFBwA==?authuser"
//    val pdfUrl = "http://www.ipu.ac.in/Pubinfo2024/seatmspk25272410a.pdf"
    if (pdfUrl == null) {
        Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
        return
    }

    // Encode the URL to handle special characters
    val encodedUrl = Uri.encode(pdfUrl, "@#&=*+-_.,:!?()/~'%")

    val downloadManager = context.getSystemService<DownloadManager>()!!
    val uri = Uri.parse(encodedUrl)
    val request = DownloadManager.Request(uri)
        .setTitle(title)
        .setDescription("Downloading")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS,
            title
        )
    val downloadId = downloadManager.enqueue(request)
    val query = DownloadManager.Query().setFilterById(downloadId)

    val notificationManager = NotificationManagerCompat.from(context)

    scope.launch {
        createNotificationChannel(context)
        while (true) {
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        // Download completed
                        val localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        Log.i("STATUS_SUCCESSFUL 1", "STATUS_SUCCESSFUL")
                        Log.i("STATUS_SUCCESSFUL 2", localUri)
                        // Toast.makeText(context, localUri, Toast.LENGTH_SHORT).show() // Why this shitty Toast appearing again nd again ?????????
                        notificationManager.cancel(notificationId)
                        break
                    }
                    DownloadManager.STATUS_FAILED -> {
                        // Download failed
                        Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                        notificationManager.cancel(notificationId)
                        break
                    }
                    else -> {
                        // Download in progress
                        val bytesDownloaded =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                        showDownloadNotification(
                            context,
                            notificationManager,
                            notificationId,
                            bytesDownloaded,
                            bytesTotal,
                            activity
                        )
                    }
                }
            }
            cursor.close()
            delay(1000) // Update the notification every second
        }
    }
}


private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "download_channel",
            "Download Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

private fun showDownloadNotification(
    context: Context,
    notificationManager: NotificationManagerCompat,
    notificationId: Int,
    bytesDownloaded: Int,
    bytesTotal: Int,
    activity: ComponentActivity?
) {

    val progress = (bytesDownloaded.toFloat() / bytesTotal.toFloat() * 100).toInt()

    val builder = NotificationCompat.Builder(context, "download_channel")
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle("Downloading PDF")
        .setContentText("$progress% downloaded")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOnlyAlertOnce(true)
        .setProgress(100, progress, false)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        val launcher = activity?.activityResultRegistry?.register(
            "requestPermissionKey",
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, now we can send the notification
                notificationManager.notify(notificationId, builder.build())
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
//                Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        launcher?.launch(Manifest.permission.POST_NOTIFICATIONS)
        return
    }
    notificationManager.notify(notificationId, builder.build())
}