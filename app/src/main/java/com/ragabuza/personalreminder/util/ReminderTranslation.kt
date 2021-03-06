package com.ragabuza.personalreminder.util

import android.content.Context
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.Reminder
import android.location.Geocoder
import com.ragabuza.personalreminder.util.Constants.Other.Companion.CONTACT_PREFIX
import java.io.IOException
import java.util.*


/**
 * Created by diego.moyses on 1/24/2018.
 */
class ReminderTranslation(val context: Context) {
    fun toCode(string: String): String {
        return when (string) {
            context.getString(R.string.when_is) -> Reminder.IS
            context.getString(R.string.when_isnot) -> Reminder.ISNOT
            else -> Reminder.IS
        }
    }

    fun toString(string: String): String {
        return when (string) {
            Reminder.IS -> context.getString(R.string.when_is)
            Reminder.ISNOT -> context.getString(R.string.when_isnot)
            else -> ""
        }
    }

    fun getNotification(reminder: Reminder): String{
        return when(reminder.type){
            Reminder.WIFI -> "${toString(reminder.rWhen)} ${reminder.condition}"
            Reminder.BLUETOOTH -> "${toString(reminder.rWhen)} ${reminder.condition}"
            Reminder.TIME -> TimeString(context, Calendar.getInstance()).getSimple()
            Reminder.LOCATION -> "${context.getString(R.string.you_are_in)} ${reminder.rWhen}"
            else -> reminder.condition
        }
    }
    fun getViewer(reminder: Reminder): String{
        return when(reminder.type){
            Reminder.WIFI -> "${toString(reminder.rWhen)} ${reminder.condition}"
            Reminder.BLUETOOTH -> "${toString(reminder.rWhen)} ${reminder.condition}"
            Reminder.TIME -> TimeString(context, Calendar.getInstance()).getSimple()
            Reminder.LOCATION -> reminder.rWhen
            else -> reminder.condition
        }
    }

    fun reminderType(string: String): String {
        return when (string) {
            Reminder.WIFI -> context.getString(R.string.trans_wifi)
            Reminder.BLUETOOTH -> context.getString(R.string.trans_blue)
            Reminder.TIME -> context.getString(R.string.trans_time)
            Reminder.LOCATION -> context.getString(R.string.trans_loc)
            Reminder.SIMPLE -> context.getString(R.string.trans_reminder)
            else -> ""
        }
    }

    fun reminderIcon(string: String): Int {
        return when (string) {
            Reminder.WIFI -> R.drawable.ic_wifi_white
            Reminder.BLUETOOTH -> R.drawable.ic_bluetooth_white
            Reminder.TIME -> R.drawable.ic_time_white
            Reminder.LOCATION -> R.drawable.ic_location_white
            Reminder.SIMPLE -> R.drawable.ic_simple_white
            else -> R.drawable.ic_simple_white
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

    fun extraIsLink(extra: String): Boolean{
        val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
        return extra.matches(regex)
    }
    fun extraIsContact(extra: String): Boolean{
        return extra.contains(CONTACT_PREFIX)
    }

    fun parseContact(extra: String): String{
        return extra.substring(9 until extra.length)
    }
    fun parsePhone(extra: String): String{
        val regex = Regex("[^0-9]")
        return extra.replace(regex, "")
    }

}