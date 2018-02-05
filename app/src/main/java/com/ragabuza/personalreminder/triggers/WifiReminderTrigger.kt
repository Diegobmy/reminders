package com.ragabuza.personalreminder.triggers

import android.content.Context
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.NotificationHelper
import com.ragabuza.personalreminder.util.TimeString
import java.util.*

/**
 * Created by diego.moyses on 1/29/2018.
 */
class WifiReminderTrigger(context: Context): BaseTrigger(context) {

    fun connected(web: String){
        val dao = ReminderDAO(context)
        val reminders = dao.getActive(web, Reminder.IS)
        dao.close()
        notifyReminders(reminders)
    }
    fun disconnected(web: String){
        val dao = ReminderDAO(context)
        val reminders = dao.getActive(web, Reminder.ISNOT)
        dao.close()
        notifyReminders(reminders)
    }
    private fun notifyReminders(reminders: List<Reminder>) {
        reminders.forEach {
            setDone(it)
            NotificationHelper(context).showNotification(it)
        }
    }


}