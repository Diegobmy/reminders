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
class LocationReminderTrigger(context: Context): BaseTrigger(context) {

    fun inRange(location: Location){
        val condition = "${location.latitude},${location.longitude}"
        val dao = ReminderDAO(context)
        val reminders = dao.getActive(Reminder.LOCATION, condition, null)
        dao.close()
        notifyReminders(reminders)
    }
    private fun notifyReminders(reminders: List<Reminder>) {
        reminders.forEach {
            setDone(it)
            NotificationHelper(context).showNotification(it)
        }
        refreshIfActive()
    }


}