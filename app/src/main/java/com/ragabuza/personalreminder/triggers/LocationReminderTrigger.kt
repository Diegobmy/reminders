package com.ragabuza.personalreminder.triggers

import android.content.Context
import android.location.Location
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.NotificationHelper
import com.ragabuza.personalreminder.util.TimeString
import java.util.*

/**
 * Created by diego.moyses on 1/29/2018.
 */
class LocationReminderTrigger(val context: Context) {

    fun inRange(location: Location){
        val condition = "${location.latitude},${location.longitude}"
        val dao = ReminderDAO(context)
        val reminders = dao.getActive(condition, Reminder.IS)
        dao.close()
        notifyReminders(reminders)
    }
    private fun notifyReminders(reminders: List<Reminder>) {
        val dao = ReminderDAO(context)
        reminders.forEach {
            it.active = false
            it.done = TimeString(Calendar.getInstance()).getSimple()
            dao.alt(it)
            val connect = context.getString(R.string.you_are_in)
            NotificationHelper(context).showNotification(it.id.toInt(), it.reminder, "$connect ${it.rWhen}")
        }
        dao.close()
    }


}