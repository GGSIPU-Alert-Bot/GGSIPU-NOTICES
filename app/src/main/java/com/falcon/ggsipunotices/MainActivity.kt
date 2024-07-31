package com.falcon.ggsipunotices

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.FileProvider
import com.falcon.ggsipunotices.ui.NoticeListScreen
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val downloadReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                val query = DownloadManager.Query().setFilterById(downloadId)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    val downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
                        val uri = Uri.parse(downloadLocalUri)
                        saveFileToPrivateStorage(context, uri, downloadMimeType)
                    }
                }
                cursor.close()
            }
        }
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge() // Removed in order to bring status bar
        setContent {
            NoticeListScreen(
                startDownloading = ::startDownloading,
                openFile = ::openFile,
                shareFile = ::shareFile,
                checkFileExists = ::checkFileExists
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }
    private fun saveFileToPrivateStorage(context: Context, uri: Uri, mimeType: String) {
        val inputFile = File(uri.path ?: return)
        val privateDir = context.filesDir
        val outputFile = File(privateDir, inputFile.name)

        try {
            FileInputStream(inputFile).use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            Toast.makeText(context, "File saved to private storage", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to save file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun startDownloading(context: Context, fileURL: String?, titleAndFileName: String) {
        val fileURL = "https://github.com/labmember003/usar_data/raw/master/YEAR_1/Sem1/EngineeringMechanics/paper/MinorExam.pdf" // TODO: Remove This Line
        try {
            if (fileURL.isNullOrEmpty()) {
                Toast.makeText(context, "INVALID URL DETECTED", Toast.LENGTH_SHORT).show()
                return
            }
            val request = DownloadManager.Request(Uri.parse(fileURL))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(titleAndFileName)
                .setDescription("File is downloading")
                .setMimeType("application/pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, titleAndFileName)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Download has begun, see notifications", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun openFile(context: Context, fileName: String) {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/pdf")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No app to open this file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun shareFile(context: Context, fileName: String) {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_SEND)
                .setType("application/pdf")
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(Intent.createChooser(intent, "Share File"))
        } else {
            Toast.makeText(context, "First download the file", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkFileExists(context: Context, fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.exists()
    }












//    private fun openFile(file: File) {
//        if (checkFileExists(context, fileName)) {
//            openFile(this, fileName)
//        } else {
//            startDownloading(this, fileURL, fileName)
//        }
//    }
//










    // Request code for selecting a PDF document.
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    fun startDownloading(fileURL: String?, titleAndFileName: String) {
//        val fileURL = "https://github.com/labmember003/usar_data/raw/master/YEAR_1/Sem1/EngineeringMechanics/paper/MinorExam.pdf" // TODO: Remove This Line
//        val activity = this
//        try {
//            if (fileURL == null) {
//                Toast.makeText(this, "INVALID URL DETECTED", Toast.LENGTH_SHORT).show()
//                return
//            } else if (fileURL.isNotEmpty()) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    activity.registerReceiver(
//                        attachmentDownloadCompleteReceive,
//                        IntentFilter(
//                            DownloadManager.ACTION_DOWNLOAD_COMPLETE
//                        ),
//                        Context.RECEIVER_EXPORTED
//                    )
//                } else {
//                    activity.registerReceiver(
//                        attachmentDownloadCompleteReceive,
//                        IntentFilter(
//                            DownloadManager.ACTION_DOWNLOAD_COMPLETE
//                        )
//                    )
//                }
//                val request = DownloadManager.Request(Uri.parse(fileURL))
//                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//                request.setTitle(titleAndFileName)
//                request.setDescription("File is donwloading")
//                request.setMimeType("application/pdf")
//                request.allowScanningByMediaScanner()
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, titleAndFileName)
//                Toast.makeText(
//                    baseContext,
//                    "Download has begun, See Notifications",
//                    Toast.LENGTH_LONG
//                ).show()
//                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                manager.enqueue(request)
//            } else {
//                Toast.makeText(this, "INVALID URL DETECTED", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(
//                activity,
//                e.message.toString(),
//                Toast.LENGTH_SHORT
//            ).show()
//            return
//        }
//    }

//    private fun openFile(file: File) {
////        file.toURI()
//        openDownloadedAttachment(this, Uri.fromFile(file), "application/pdf")
//
//
////        if (file.exists()) {
////            val myIntent = Intent(Intent.ACTION_VIEW)
////            val fileProviderUri =
////                FileProvider.getUriForFile(this, this.application.packageName + ".provider", file)
////            myIntent.data = fileProviderUri
////            myIntent.setDataAndType(fileProviderUri, "application/pdf")
////            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMFfaISSION)
////            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////            val j = Intent.createChooser(myIntent, "Choose an application to open with:")
////            startActivity(j)
////        } else {
////            Toast.makeText(
////                baseContext,
////                "File not found",
////                Toast.LENGTH_LONG
////            ).show()
////        }
//    }

//    private var attachmentDownloadCompleteReceive: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
//                val downloadId = intent.getLongExtra(
//                    DownloadManager.EXTRA_DOWNLOAD_ID, 0
//                )
//                openDownloadedAttachment(context, downloadId)
//            }
//        }
//    }
//    private fun openDownloadedAttachment(context: Context, downloadId: Long) {
//        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//        val query = DownloadManager.Query()
//        query.setFilterById(downloadId)
//        val cursor: Cursor = downloadManager.query(query)
//        if (cursor.moveToFirst()) {
//            val downloadStatus: Int =
//                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS).absoluteValue)
//            val downloadLocalUri: String =
//                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI).absoluteValue)
//            val downloadMimeType: String =
//                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE).absoluteValue)
//            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
//                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType)
//            }
//        }
//        cursor.close()
//    }

//    private fun openDownloadedAttachment(
//        context: Context,
//        attachmentUri: Uri,
//        attachmentMimeType: String
//    ) {
//        Log.i("1- openDownloadedAttachment1, attachmentUri:", attachmentUri.toString())
//        var attachmentUri: Uri? = attachmentUri
//        Log.i("2- openDownloadedAttachment1, attachmentUri:", attachmentUri?.toString() ?: "null")
//        if (attachmentUri != null) {
//            Log.i("3- openDownloadedAttachment1, attachmentUri:", attachmentUri.toString())
//            if (ContentResolver.SCHEME_FILE == attachmentUri.scheme) { // Checks if attachmentUri is file URI or content URI
//                // We need to give content URI to Intent always, because:
//                // Direct access to file URIs is restricted in modern Android versions due to security reasons. Apps are encouraged to use content URIs instead.
//                Log.i("3.5- openDownloadedAttachment1, attachmentUri:", attachmentUri.toString())
//                val file = File(attachmentUri.path)
//                attachmentUri =
//                    FileProvider.getUriForFile(this, this.application.packageName +".provider", file)
//            }
//            Log.i("4- openDownloadedAttachment1, attachmentUri:", attachmentUri?.toString() ?: "null")
//            val openAttachmentIntent = Intent(Intent.ACTION_VIEW)
//            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType)
//            openAttachmentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            openAttachmentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            openAttachmentIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            try {
//                context.startActivity(openAttachmentIntent)
//            } catch (e: ActivityNotFoundException) {
//                Toast.makeText(context, "No app to open this file", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//    private fun shareFile(fileName: String) {
//        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
//        if (file.exists()) {
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "application/pdf"
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            val photoURI = FileProvider.getUriForFile(
//                this,
//                this.applicationContext.packageName + ".provider",
//                file
//            )
//            intent.putExtra(Intent.EXTRA_STREAM, photoURI)
//            startActivity(Intent.createChooser(intent, "Share File"))
//        } else {
//            Toast.makeText(this, "First Download the file", Toast.LENGTH_SHORT).show()
//        }
//    }
}