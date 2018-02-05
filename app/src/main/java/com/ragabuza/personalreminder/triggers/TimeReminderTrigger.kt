package com.ragabuza.personalreminder.triggers

import android.content.Context
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.NotificationHelper

/**
 * Created by diego.moyses on 2/5/2018.
 */
class TimeReminderTrigger(context: Context) : BaseTrigger(context) {
    fun notifyReminders(reminder: Reminder) {
        setDone(reminder)
        NotificationHelper(context).showNotification(reminder)
        refreshIfActive()
    }
}