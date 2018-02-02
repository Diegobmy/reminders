package com.ragabuza.personalreminder.util

import android.content.Context
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.Reminder
import java.nio.file.Files.size
import android.location.Geocoder
import android.location.Location
import java.io.IOException
import java.util.*


/**
 * Created by diego.moyses on 1/24/2018.
 */
class ReminderTranslation(val context: Context) {
    fun toSave(string: String): String {
        return when (string) {
            context.getString(R.string.when_is) -> Reminder.IS
            context.getString(R.string.when_isnot) -> Reminder.ISNOT
            else -> Reminder.IS
        }
    }

    fun toString(string: String, type: String): String {
        return when (string) {
            Reminder.IS -> context.getString(R.string.when_is)
            Reminder.ISNOT -> context.getString(R.string.when_isnot)
            else -> context.getString(R.string.when_is)
        }
    }

    fun reminderType(string: String): String {
        return when (string) {
            Reminder.WIFI -> "Lembrete WiFi"
            Reminder.BLUETOOTH -> "Lembrete Bluetooth"
            Reminder.TIME -> "Lembrete agendado"
            Reminder.LOCATION -> "Lembrete de Localização"
            Reminder.SIMPLE -> "Lembrete"
            else -> ""
        }
    }

    fun locationAddress(location: String): String{
        val latitude = location.split(",")[0].toDouble()
        val longitude = location.split(",")[1].toDouble()
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val listAddresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (null != listAddresses && listAddresses.size > 0) {
                return listAddresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "$latitude,$longitude"
    }

}