package com.ragabuza.personalreminder.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.ui.ReminderList

/**
 * Created by diego.moyses on 1/15/2018.
 */
class NotificationHelper {
    fun showNotification(id: Int, p0: Context?, type: Int, description: String){

        val mNotificationManager = p0?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(p0)
                .setSmallIcon(R.drawable.ic_simple)
                .setContentTitle("BitAlarme")
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setContentText(description)
                .setDefaults(type)
        val intentDestination = Intent(p0, ReminderList::class.java)
        val pi = PendingIntent.getActivity(p0, 0, intentDestination, 0)
        mBuilder.setContentIntent(pi)
        val mNotification = mBuilder.build()
        mNotification.flags = mNotification.flags or (Notification.FLAG_AUTO_CANCEL or Notification.DEFAULT_SOUND)
        mNotificationManager.notify(id, mNotification)

    }
}