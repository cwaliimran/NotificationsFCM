package com.cwnextgen.fcmtest.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.cwnextgen.fcmtest.MainActivity
import com.cwnextgen.fcmtest.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MyFirebaseInstanceService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var title = ""
        var body = ""
        var dataPayload = ""

        // Handle the data payload of the incoming message
        if (remoteMessage.data.isNotEmpty()) {
            title = remoteMessage.data["title"].toString()
            body = remoteMessage.data["body"].toString()
            dataPayload = remoteMessage.data.toString() // Full data payload as a string
        }

        if (remoteMessage.notification != null) {
            title = remoteMessage.notification?.title.toString()
            body = remoteMessage.notification?.body.toString()
        }

        // Create an intent for the notification
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("title", title)
            putExtra("body", body)
            putExtra("dataPayload", dataPayload)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 100, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // Show notification
        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Default", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
