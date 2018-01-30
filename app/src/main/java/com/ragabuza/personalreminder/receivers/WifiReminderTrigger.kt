package com.ragabuza.personalreminder.receivers

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
class WifiReminderTrigger(val context: Context) {

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
        val dao = ReminderDAO(context)
        reminders.forEach {
            it.active = false
            it.done = TimeString(Calendar.getInstance()).getSimple()
            dao.alt(it)
            val connect = if(it.rWhen == Reminder.IS) context.getString(R.string.contected_to) else context.getString(R.string.descontected_to)
            NotificationHelper(context).showNotification(it.id.toInt(), it.reminder, "$connect ${it.condition}")
        }
        dao.close()
    }


}