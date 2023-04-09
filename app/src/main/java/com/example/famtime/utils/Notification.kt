package com.example.famtime.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.famtime.R

const val notificationID = 1
const val channelId = "channel1"
const val titleExtra = "tittleExtra"
const val messageExtra = "messageExtra"

class Notification {



    class Notification: BroadcastReceiver(){

        override fun onReceive(context: Context, intent: Intent) {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.famtime_icon)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(messageExtra))
                .build()

            val manager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(notificationID,notification)

        }

    }
}