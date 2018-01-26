package com.ragabuza.personalreminder.util

import java.util.*

/**
 * Created by diego.moyses on 1/23/2018.
 */
class TimeString(private val date: Calendar) {
    fun getString(hourSensitive: Boolean = false): String {
        val completeDay: String

        val day = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH) + 1
        val hour = date.get(Calendar.HOUR_OF_DAY)
        val minute = date.get(Calendar.MINUTE)

        val now = Calendar.getInstance()
        completeDay = when (date.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR)) {
            0 -> "Hoje"
            1 -> "Amanhã"
            else -> "${if (day < 10) "0" else ""}$day/${if (month < 10) "0" else ""}$month"
        }

        val intervalMin = (date.timeInMillis - now.timeInMillis) / 60000
        val intervalHalfHour = (date.timeInMillis - now.timeInMillis) / 1800000
        val intervalHour = intervalHalfHour / 2

        return when {
            hourSensitive && intervalMin < 60 && intervalMin > 0 -> "Em $intervalMin minuto${if(intervalMin> 1) "s" else ""}"
            hourSensitive && intervalHalfHour < 10 && intervalHalfHour > 0 -> if(intervalHalfHour.isOdd()) "Em $intervalHour hora${if(intervalHour > 1) "s" else ""} e meia" else "Em $intervalHour horas"
            else -> "$completeDay, ${if (hour < 10) "0" else ""}$hour:${if (minute < 10) "0" else ""}$minute"
        }
    }
    private fun Long.isOdd(): Boolean{
        return this % 2L != 0L
    }
}