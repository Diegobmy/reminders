package com.ragabuza.personalreminder.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ragabuza.personalreminder.receivers.TimeReceiver

/**
 * Created by diego.moyses on 2/2/2018.
 */
class AlarmHelper(private val context: Context?) {

    private val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, TimeReceiver::class.java)

    fun setAlarm(id: Long, time: Long){
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, getIntent(id))
    }

    fun stopAlarm(id: Long){
        alarmManager.cancel(getIntent(id))
    }

    private fun getIntent(id: Long): PendingIntent{
        val intentNew = alarmIntent
        intentNew.putExtra("reminderID", id)
        return PendingIntent.getBroadcast(context, id.toInt(), intentNew, 0)
    }

}