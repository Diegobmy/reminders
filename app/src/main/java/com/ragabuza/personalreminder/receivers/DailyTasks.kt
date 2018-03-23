package com.ragabuza.personalreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.util.Shared
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by diego.moyses on 2/27/2018.
 */
class DailyTasks: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null)
        if (Shared(p0).isDeleteOld()) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)

            val dao = ReminderDAO(p0)

            val reminders = dao.getOld()

            val sdf = SimpleDateFormat("dd/MM/yy, hh:mm", Locale.getDefault())

            reminders.forEach {
                if (sdf.parse(it.done).time <= cal.timeInMillis)
                    dao.del(it)
            }
        }
    }
}