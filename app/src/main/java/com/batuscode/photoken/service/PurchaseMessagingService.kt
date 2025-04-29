package com.batuscode.photoken.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.batuscode.photoken.AiActivity
import com.batuscode.photoken.R
import com.batuscode.photoken.utils.Auth
import com.batuscode.photoken.utils.FunctionsUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PurchaseMessagingService : FirebaseMessagingService() {
    val TAG = "MessagingService"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG , "onNewToken :: " + token)
        CoroutineScope(Dispatchers.IO).launch {
            FunctionsUtil.sendMSGtoken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG , "From ${message.from}")

        if (message.data.isNotEmpty()){
            Log.d(TAG, "Message data payload: ${message.data}")
            val type = message.data.get("type").toString()
            Log.d(TAG , "Message data type :: ${type}")

            if (type.equals("subs")){
                Auth.auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                    if (!task.isSuccessful){
                        return@addOnCompleteListener
                    }

                    Log.d(TAG , "auth id token successfully enforced to refresh")
                }
            }
        }

        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            val body = it.body
            val title = it.title
            sendNotification(body!!,title!!)
        }
    }

    private fun sendNotification(messageBody: String , title : String) {
        val intent = Intent(this, AiActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "app_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.defult_notification_icon)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}