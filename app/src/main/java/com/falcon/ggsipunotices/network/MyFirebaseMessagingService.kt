package com.falcon.ggsipunotices.network

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.falcon.ggsipunotices.MainActivity
import com.falcon.ggsipunotices.R
import com.falcon.ggsipunotices.model.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var fcmApiHelper: FcmApiHelper

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    init {
        Log.i("FCM", "FCM init")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Push to server
        sendRegistrationToServer(token)
    }

    @SuppressLint("HardwareIds")
    private fun sendRegistrationToServer(token: String) {
        // Implement the logic to send the token to your server
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.i("FCM Token: ", token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = fcmApiHelper.sendFcmToken(
                    deviceId = deviceId,
                    token = FcmTokenRequest(token)
                )
                if (response.isSuccessful) {
                    Log.i("FCM", "Token sent to server successfully")
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(appContext, "Failed to connect to server", Toast.LENGTH_SHORT).show()
                        Log.i("FCM Error 1", response.errorBody()?.string() ?: "Unknown error")
                        Log.i("FCM Error 2", response.message())
                    }
                }
            } catch (e: Exception) {
                Log.i("FCM Error:", e.message.toString())
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let { notification ->
            val title: String
            val body: String

            val data = remoteMessage.data
            val isPriority = data["isPriority"] == "true"
            val noticeIds = data["noticeIds"]?.split(",") ?: listOf()

            if (isPriority) {
                title = notification.title ?: "New Notice"
                body = notification.body ?: "You have a new notice"
            } else {
                title = notification.title ?: "New Notices"
                body = notification.body ?: "You have new notices"
            }

//            val timestamp = data["timestamp"]?.toLongOrNull()
//            val latestTimestamp = data["latestTimestamp"]?.toLongOrNull()

            if (isPriority) {
                handlePriorityNotice(title, body, noticeIds)
            } else {
                val updateCount = data["updateCount"]?.toIntOrNull() ?: noticeIds.size
                handleBundledNotices(title, body, noticeIds, updateCount)
            }
        }
    }

    private fun handlePriorityNotice(title: String, body: String, noticeIds: List<String>) {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            putStringArrayListExtra("noticeIds", ArrayList(noticeIds))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        showNotification(title, body, intent, true, noticeIds.size)
    }

    private fun handleBundledNotices(title: String, body: String, noticeIds: List<String>, updateCount: Int) {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            putExtra("updateCount", updateCount)
            putStringArrayListExtra("noticeIds", ArrayList(noticeIds))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        showNotification(title, body, intent, false, noticeIds.size) // Using a fixed ID for bundled notifications
    }

    private fun showNotification(title: String, body: String, intent: Intent, isPriority: Boolean, notificationId: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = if (isPriority) "priority_channel" else "default_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.notes_grey)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(0, notificationBuilder.build())
}