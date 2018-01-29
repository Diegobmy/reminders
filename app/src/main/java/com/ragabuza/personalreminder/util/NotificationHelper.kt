package com.ragabuza.personalreminder.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.ui.ReminderList

/**
 * Created by diego.moyses on 1/15/2018.
 */
class NotificationHelper(val context: Context) {
    fun showNotification(id: Int, title: String, description: String){

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_simple)
                .setContentTitle(title)
//                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setColor(context.resources.getColor(R.color.colorPrimary))
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_ALL)
        val intentDestination = Intent(context, ReminderList::class.java)
        val pi = PendingIntent.getActivity(context, 0, intentDestination, 0)
        mBuilder.setContentIntent(pi)
        val mNotification = mBuilder.build()
        mNotification.flags = mNotification.flags or (Notification.FLAG_AUTO_CANCEL or Notification.DEFAULT_SOUND)
        mNotificationManager.notify(id, mNotification)

    }
}