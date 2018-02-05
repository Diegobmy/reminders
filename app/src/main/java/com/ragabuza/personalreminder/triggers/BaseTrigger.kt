package com.ragabuza.personalreminder.triggers

import android.content.Context
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder

/**
 * Created by diego.moyses on 2/5/2018.
 */
open class BaseTrigger(val context: Context) {
    fun setDone(reminder: Reminder){
        val DAO = ReminderDAO(context)
        reminder.done = "WAITING"
        DAO.alt(reminder)
        DAO.close()
    }
}