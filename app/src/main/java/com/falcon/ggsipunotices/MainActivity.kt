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
import android.provider.Settings
import android.util.Log
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.falcon.ggsipunotices.settings.SettingsScreen
import com.falcon.ggsipunotices.ui.NoticeListScreen
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("HardwareIds")
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge() // Removed in order to bring status bar
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.i("com.falcon.ggsipunotices.model.Device ID", deviceId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
        setContent {
            val navController = rememberNavController()
            val modalSheetState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
                skipHalfExpanded = true
            )
            val preferencesList = listOf("All", "High Priority")
            val collegePreferenceList = listOf(
                "all",
                "ADGITM (Northern India Engineering College)",
                "Army College of Medical Sciences (ACMS)",
                "Atal Bihari Vajpayee Institute of Medical Sciences and Dr. Ram Manohar Lohia Hospital (ABVIMS & RMLH)",
                "B. M. Institute of Engineering & Technology (BMIET)",
                "Banarsidas Chandiwala Institute of Management and Technology (BCIMT)",
                "Bhai Parmanand Institute of Business Studies (BPIBS)",
                "Bhagwan Mahaveer College of Engineering and Management (BMCE&M)",
                "Ch. Brahm Prakash Ayurved Charak Sansthan (CBPACS)",
                "C-DAC, Noida (CDAC)",
                "Delhi Institute of Heritage Research & Management (DIHRM)",
                "Delhi Institute of Technology & Management (DITM)",
                "Delhi Metropolitan Education (DME)",
                "Delhi Technical Campus (DTC)",
                "Dr. Baba Saheb Ambedkar Medical College and Hospital (BSAMCH)",
                "DSPSR (Delhi School of Professional Studies and Research)",
                "Fairfield Institute of Management and Technology (FIMT)",
                "Guru Tegh Bahadur Institute of Technology (GTBIT)",
                "Institute of Information Technology and Management (IITM)",
                "Jagan Institute of Management Studies (JIMS)",
                "Kalka Institute of Education and Research (KIER)",
                "KCC Institute of Legal and Higher Education (KCCILHE)",
                "Maharaja Agrasen Institute of Management Studies (MAIMS)",
                "Maharaja Agrasen Institute of Technology (MAIT)",
                "Maharaja Surajmal Institute (MSI)",
                "Maharaja Surajmal Institute of Technology (MSIT)",
                "Management Education & Research Institute (MERI)",
                "National Power Training Institute (NPTI)",
                "New Delhi Institute of Management (NDIM)",
                "North Delhi Municipal Corporation Medical College (NDMCMC)",
                "Rukmini Devi Institute of Advanced Studies (RDIAS)",
                "Sirifort Institute of Management Studies (SIMS)",
                "Tecnia Institute of Advanced Studies (TIAS)",
                "Trinity Institute of Professional Studies (TIPS)",
                "University School of Architecture and Planning (USAP)",
                "University School of Automation and Robotics (USAR)",
                "University School of Basic & Applied Sciences (USBAS)",
                "University School of Biotechnology (USBT)",
                "University School of Chemical Technology (USCT)",
                "University School of Design & Innovation (USDI)",
                "University School of Environment Management (USEM)",
                "University School of Humanities & Social Sciences (USHSS)",
                "University School of Information and Communication Technology (USICT)",
                "University School of Law and Legal Studies (USLLS)",
                "University School of Management Studies (USMS)",
                "University School of Mass Communication (USMC)",
                "University School of Medicine and Para-Medical Health Sciences (USMPMHS)",
                "Vardhman Mahavir Medical College and Safdarjung Hospital (VMMC & SJH)",
                "Vivekananda Institute of Professional Studies (VIPS)"
            )

            val fcmNoticeId = intent.getStringArrayListExtra("noticeIds")

            NavHost(navController = navController, startDestination = "main_screen") {
                composable("main_screen") {
                    ModalBottomSheetLayout(
                        sheetState = modalSheetState,
                        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        sheetContent = {
                            MainScreenBottomSheetContent(modalSheetState, navController)
                        }
                    ) {
                        NoticeListScreen(
                            openFile = ::openFile,
                            shareFile = ::shareFile,
                            modalSheetState = modalSheetState,
                            fcmNoticeIdList = fcmNoticeId
                        )
                    }
                }
                composable("settings") {
                    SettingsScreen(
                        notificationPreferenceList = preferencesList,
                        collegePreferenceList = collegePreferenceList,
                        deviceId = deviceId
                    ) {
                        navController.popBackStack()
                    }
                }
                composable("result") {
                    WebViewScreen()
                }
            }
        }
        // Handle the notification intent
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            val title = it.getStringExtra("notification_title")
            val body = it.getStringExtra("notification_body")

            // Use the title and body as needed
            if (title != null && body != null) {
                // Handle navigation or any other action here
                println("Notification clicked with title: $title and body: $body")
                // Example: Navigate to a specific screen or show a dialog
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MainScreenBottomSheetContent(
        modalSheetState: ModalBottomSheetState,
        navController: NavHostController
    ) {
        DrawerContent(navController, modalSheetState)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun DrawerContent(navController: NavHostController, modalSheetState: ModalBottomSheetState) {
        val scope = rememberCoroutineScope()
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menu",
                    style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                    fontSize = 20.sp
                )
                androidx.compose.material.Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .clickable {
                            scope.launch { modalSheetState.hide() }
                        }
                )
            }
            NavDrawerContent("Settings", R.drawable.baseline_settings_24) {
                navController.navigate("settings")
            }
            Spacer(modifier = Modifier.height(2.dp))
            NavDrawerContent("Result", R.drawable.ic_baseline_result_right_alt_24) {
                navController.navigate("result")
            }
        }

    }

    @Composable
    fun NavDrawerContent(contentName: String, imageID: Int, onClick: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = imageID),
                contentDescription = "" ,
                modifier = Modifier
                    .size(30.dp)
            )
            Spacer(
                modifier = Modifier
                    .size(10.dp)
            )
            Text(
                text = contentName,
                style = androidx.compose.material.MaterialTheme.typography.body1,
                color = Color.Unspecified
            )
        }
    }

    private fun openFile(context: Context, file: File, fileName: String, pdfUrl: String?, notificationId: Int, scope: CoroutineScope) {
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
        } else {
            downloadAndOpenPdfNotification(fileName, context, pdfUrl, notificationId, scope, file)
        }
    }
    private fun shareFile(file: File, fileName: String, context: Context, pdfUrl: String?, notificationId: Int, scope: CoroutineScope) {
        if (file.exists()) {
            val attachmentUri = FileProvider.getUriForFile(
                this,
                this.application.packageName +".provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/pdf"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_STREAM, attachmentUri)
            this.startActivity(Intent.createChooser(intent, "Share File"))
        } else {
            Log.i("MainActivity", "File Not Downloaded, Initiating download first")
            downloadAndSharePdfNotification(fileName, context, pdfUrl, notificationId, scope, file)
        }
    }
    private fun requestNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                Log.i("Notification", "Permission already granted")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // Show UI explaining why the permission is needed
            }
            else -> {
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted
                Log.i("Notification", "Permission granted")
            } else {
                // Permission denied
                Log.i("Notification", "Permission denied")
                Toast.makeText(this, "Permission denied, You Won't Be Notified of Any New Notice Published", Toast.LENGTH_LONG).show()
            }
        }
}

fun downloadAndSharePdfNotification(
    title: String,
    context: Context,
    pdfUrl: String?,
    notificationId: Int,
    scope: CoroutineScope,
    file: File,
) {
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
                        // Share Code
                        val attachmentUri = FileProvider.getUriForFile(
                            context,
                            context.packageName +".provider",
                            file
                        )
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "application/pdf"
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        intent.putExtra(Intent.EXTRA_STREAM, attachmentUri)
                        context.startActivity(Intent.createChooser(intent, "Share File"))
                        // Share Code Ends
                    }
                } else {
                    Toast.makeText(context, "1:"+response.message, Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                    notificationManager.cancel(notificationId)
                }
            } catch (e: Exception) {
                Log.e("DPN - 1", e.toString())
                e.printStackTrace()
                notificationManager.cancel(notificationId)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

fun downloadAndOpenPdfNotification(
    title: String,
    context: Context,
    pdfUrl: String?,
    notificationId: Int,
    scope: CoroutineScope,
    file: File,
) {
    Log.i("DPN", pdfUrl.toString())
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
                        // File Open Code
                        if (file.exists()) {
                            var attachmentUri = FileProvider.getUriForFile(
                                context,
                                context.packageName +".provider",
                                file
                            )
                            if (ContentResolver.SCHEME_FILE == attachmentUri.scheme) { // Checks if attachmentUri is file URI or content URI
                                // We need to give content URI to Intent always, because:
                                // Direct access to file URIs is restricted in modern Android versions due to security reasons. Apps are encouraged to use content URIs instead.
                                Log.i("3.5- openDownloadedAttachment1, attachmentUri:", attachmentUri.toString())
                                val file = File(attachmentUri.path)
                                attachmentUri =
                                    FileProvider.getUriForFile(context, context.packageName +".provider", file)
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
                        // Open File Code Ends
                    }
                } else {
                    Toast.makeText(context, "1:"+response.message, Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                    notificationManager.cancel(notificationId)
                }
            } catch (e: Exception) {
                Log.e("DPN - 1", e.toString())
                e.printStackTrace()
                notificationManager.cancel(notificationId)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                }
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

@SuppressLint("InlinedApi")
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen() {
    val url = "https://www.ipuranklist.com/student"
    val webViewState = rememberWebViewState(url)
    var isWebViewVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        WebView(
            state = webViewState,
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
                webView.settings.userAgentString = "Android"
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isWebViewVisible = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}