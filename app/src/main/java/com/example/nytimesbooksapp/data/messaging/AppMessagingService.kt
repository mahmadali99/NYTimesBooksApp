package com.example.nytimesbooksapp.data.messaging

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.nytimesbooksapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class AppMessagingService: FirebaseMessagingService()

{
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onNewToken(token: String) {
        Log.d("FCM", "New token: $token")
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    override fun onMessageReceived(message: RemoteMessage)
    {
        super.onMessageReceived(message)

        val title = message.notification?.title?: " "
        val body = message.notification?.body?:" "
     showNotification(title,body)

    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String, message: String){
        val channelId = " channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                channelId,
                "Nytimes is Notifying",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.new_york_times)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(1 , notification)
    }
}
