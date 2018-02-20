package com.ragabuza.personalreminder.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.ui.ReminderViewer
import com.ragabuza.personalreminder.util.Constants.Other.Companion.PRIVATE_FOLDER

/**
 * Created by diego.moyses on 1/15/2018.
 */
class NotificationHelper(val context: Context) {
    fun showNotification(reminder: Reminder) {

        val shared = Shared(context)

        if (reminder.folder == PRIVATE_FOLDER && !shared.isShowPrivateNotification()) return

        val trans = ReminderTranslation(context)
        val theme = shared.getTheme().normal

        val title = reminder.reminder
        val description = trans.getNotification(reminder)
        val id = reminder.id.toInt() + 50
        val intentDestination = Intent(context, ReminderViewer::class.java)
        intentDestination.putExtra("Reminder", reminder)
        intentDestination.putExtra("setDone", true)

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_simple)
                .setContentTitle(title)
//                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setColor(context.resources.getColor(theme))
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_ALL)

        val pi = PendingIntent.getActivity(context, id, intentDestination, 0)
        mBuilder.setContentIntent(pi)
        mBuilder.setDeleteIntent(pi)
        val mNotification = mBuilder.build()
        mNotification.flags = mNotification.flags or (Notification.FLAG_AUTO_CANCEL or Notification.DEFAULT_SOUND)
        mNotificationManager.notify(id, mNotification)

    }
    fun showNotificationRaw(id: Int = 0, title: String, description: String) {

        if (id > 50) return

        val intentDestination = Intent(context, ReminderViewer::class.java)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_simple)
                .setContentTitle(title)
//                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setColor(context.resources.getColor(R.color.colorPrimary))
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_ALL)

        val pi = PendingIntent.getActivity(context, id, intentDestination, 0)
        mBuilder.setContentIntent(pi)
        val mNotification = mBuilder.build()
        mNotification.flags = mNotification.flags or (Notification.FLAG_AUTO_CANCEL or Notification.DEFAULT_SOUND)
        mNotificationManager.notify(id, mNotification)

    }
}