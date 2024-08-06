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
    lateinit var apiHelper: ApiHelper

    @Inject
    @ApplicationContext
    lateinit var context: Context

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
        // You can use Retrofit or any other networking library for this
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val fcmTokenRequest = FcmTokenRequest(token)
        Log.i("FCM Token: ", token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.sendFcmToken(deviceId, fcmTokenRequest)
                if (response.isSuccessful) {
                    Log.i("FCM", "Token sent to server successfully")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Token sent to server successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to send token to server", Toast.LENGTH_SHORT).show()
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
        // Respond to received message
        // Handle the received message
        remoteMessage.notification?.let {
            // If it's a notification message
            showNotification(it.title ?: "New Notice", it.body ?: "Check your app for details")
        }

        // If it's a data message
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "New Notice"
            val message = remoteMessage.data["message"] ?: "Check your app for details"
            showNotification(title, message)
        }
    }
    @SuppressLint("ServiceCast")
    private fun showNotification(title: String, body: String) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_title", title)
            putExtra("notification_body", body)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notes_grey)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }

}