package com.ragabuza.personalreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.dao.WifiDAO
import com.ragabuza.personalreminder.triggers.WifiReminderTrigger
import com.ragabuza.personalreminder.util.NotificationHelper
import java.util.*


/**
 * Created by diego.moyses on 1/15/2018.
 */
class TimeReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (p0 != null) {
            val reminderDAO = ReminderDAO(p0)
            val id = intent?.getLongExtra("reminderID", 0) ?: 0
            val reminder = reminderDAO.getOne(id)
            if (reminder != null) {
                val cal = Calendar.getInstance()
                NotificationHelper(p0).showNotification(reminder.id.toInt(), reminder.reminder, cal.time.toString())
            }
        }
    }
}
