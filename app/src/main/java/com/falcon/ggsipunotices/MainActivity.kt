package com.falcon.ggsipunotices

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.falcon.ggsipunotices.ui.NoticeItemPreview
import com.falcon.ggsipunotices.ui.NoticeListScreen
import com.falcon.ggsipunotices.ui.theme.GGSIPUNOTICESTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.math.absoluteValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge() // Removed in order to bring status bar
        setContent {
            NoticeListScreen(
                startDownloading = ::startDownloading,
                openFile = ::openFile,
                shareFile = ::shareFile
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 12) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    // Request code for selecting a PDF document.
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun startDownloading(fileURL: String?, titleAndFileName: String) {
        val fileURL = "https://github.com/labmember003/usar_data/raw/master/YEAR_1/Sem1/EngineeringMechanics/paper/MinorExam.pdf" // TODO: Remove This Line
        val activity = this
        try {
            if (fileURL == null) {
                Toast.makeText(this, "INVALID URL DETECTED", Toast.LENGTH_SHORT).show()
                return
            } else if (fileURL.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    activity.registerReceiver(
                        attachmentDownloadCompleteReceive,
                        IntentFilter(
                            DownloadManager.ACTION_DOWNLOAD_COMPLETE
                        ),
                        Context.RECEIVER_EXPORTED
                    )
                } else {
                    activity.registerReceiver(
                        attachmentDownloadCompleteReceive,
                        IntentFilter(
                            DownloadManager.ACTION_DOWNLOAD_COMPLETE
                        )
                    )
                }
                val request = DownloadManager.Request(Uri.parse(fileURL))
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                request.setTitle(titleAndFileName)
                request.setDescription("File is donwloading")
                request.setMimeType("application/pdf")
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, titleAndFileName)
                Toast.makeText(
                    baseContext,
                    "Download has begun, See Notifications",
                    Toast.LENGTH_LONG
                ).show()
                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)
            } else {
                Toast.makeText(this, "INVALID URL DETECTED", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                activity,
                e.message.toString(),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
    }

    private fun openFile(file: File) {
//        file.toURI()
        openDownloadedAttachment(this, Uri.fromFile(file), "application/pdf")


//        if (file.exists()) {
//            val myIntent = Intent(Intent.ACTION_VIEW)
//            val fileProviderUri =
//                FileProvider.getUriForFile(this, this.application.packageName + ".provider", file)
//            myIntent.data = fileProviderUri
//            myIntent.setDataAndType(fileProviderUri, "application/pdf")
//            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMFfaISSION)
//            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val j = Intent.createChooser(myIntent, "Choose an application to open with:")
//            startActivity(j)
//        } else {
//            Toast.makeText(
//                baseContext,
//                "File not found",
//                Toast.LENGTH_LONG
//            ).show()
//        }
    }

    private var attachmentDownloadCompleteReceive: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0
                )
                openDownloadedAttachment(context, downloadId)
            }
        }
    }
    private fun openDownloadedAttachment(context: Context, downloadId: Long) {
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor: Cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val downloadStatus: Int =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS).absoluteValue)
            val downloadLocalUri: String =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI).absoluteValue)
            val downloadMimeType: String =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE).absoluteValue)
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType)
            }
        }
        cursor.close()
    }

    private fun openDownloadedAttachment(
        context: Context,
        attachmentUri: Uri,
        attachmentMimeType: String
    ) {
        var attachmentUri: Uri? = attachmentUri
        if (attachmentUri != null) {
            if (ContentResolver.SCHEME_FILE == attachmentUri.scheme) {
                val file = File(attachmentUri.path)
                attachmentUri =
                    FileProvider.getUriForFile(this, this.application.packageName +".provider", file)
            }
            val openAttachmentIntent = Intent(Intent.ACTION_VIEW)
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType)
            openAttachmentIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                context.startActivity(openAttachmentIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No app to open this file", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun shareFile(fileName: String) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/pdf"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val photoURI = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                file
            )
            intent.putExtra(Intent.EXTRA_STREAM, photoURI)
            startActivity(Intent.createChooser(intent, "Share File"))
        } else {
            Toast.makeText(this, "First Download the file", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GGSIPUNOTICESTheme {
        Greeting("Android")
    }
}