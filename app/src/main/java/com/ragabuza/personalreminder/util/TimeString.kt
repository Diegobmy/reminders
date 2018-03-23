package com.ragabuza.personalreminder.util

import android.content.Context
import com.ragabuza.personalreminder.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by diego.moyses on 1/23/2018.
 */
class TimeString(val context: Context, private val date: Calendar) {
    fun getSimple(): String {
        return SimpleDateFormat("dd/MM, hh:mm", Locale.getDefault()).format(date.time)
    }

    fun getDone(): String {
        return SimpleDateFormat("dd/MM/yy, hh:mm", Locale.getDefault()).format(date.time)
    }

    fun getString(hourSensitive: Boolean = false): String {
        val completeDay: String

        val day = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH) + 1
        val hour = date.get(Calendar.HOUR_OF_DAY)
        val minute = date.get(Calendar.MINUTE)

        val now = Calendar.getInstance()
        completeDay = when (date.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR)) {
            0 -> context.getString(R.string.today)
            1 -> context.getString(R.string.tomorrow)
            else -> "${if (day < 10) "0" else ""}$day/${if (month < 10) "0" else ""}$month"
        }

        val intervalMin = (date.timeInMillis - now.timeInMillis) / 60000
        val intervalHalfHour = (date.timeInMillis - now.timeInMillis) / 1800000
        val intervalHour = intervalHalfHour / 2

        return when {
            hourSensitive && intervalMin < 60 && intervalMin > 0 -> "${context.getString(R.string.inj)} $intervalMin ${context.getString(R.string.minute)}${if (intervalMin > 1) "s" else ""}"
            hourSensitive && intervalHalfHour < 10 && intervalHalfHour > 0 -> if (intervalHalfHour.isOdd()) "${context.getString(R.string.inj)} $intervalHour ${context.getString(R.string.hour)}${if (intervalHour > 1) "s" else ""} ${context.getString(R.string.andhalf)}" else "${context.getString(R.string.inj)} $intervalHour ${context.getString(R.string.hour)}s"
            else -> "$completeDay, ${if (hour < 10) "0" else ""}$hour:${if (minute < 10) "0" else ""}$minute"
        }
    }

    private fun Long.isOdd(): Boolean {
        return this % 2L != 0L
    }
}