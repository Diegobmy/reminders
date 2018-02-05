package com.ragabuza.personalreminder.triggers

import android.content.Context
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.ui.ReminderList

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
    fun refreshIfActive(){
        if (context == ReminderList().baseContext)
            (context as ReminderList).refreshList()
    }
}