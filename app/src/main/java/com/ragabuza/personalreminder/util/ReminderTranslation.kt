package com.ragabuza.personalreminder.util

import android.content.Context
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.Reminder

/**
 * Created by diego.moyses on 1/24/2018.
 */
class ReminderTranslation(val context: Context) {
    fun toSave(string: String): String {
        return when (string) {
            context.getString(R.string.when_is) -> Reminder.IS
            context.getString(R.string.when_isnot) -> Reminder.ISNOT
            context.getString(R.string.when_is_2) -> Reminder.IS
            context.getString(R.string.when_isnot_2) -> Reminder.ISNOT
            else -> Reminder.IS
        }
    }

    fun toString(string: String, type: String): String {
        return if (type != Reminder.LOCATION)
            when (string) {
                Reminder.IS -> context.getString(R.string.when_is)
                Reminder.ISNOT -> context.getString(R.string.when_isnot)
                else -> context.getString(R.string.when_is)
            }
        else
            when (string) {
                Reminder.IS -> context.getString(R.string.when_is_2)
                Reminder.ISNOT -> context.getString(R.string.when_isnot_2)
                else -> context.getString(R.string.when_is_2)
            }
    }

    fun reminderType(string: String): String{
        return when (string){
            Reminder.WIFI -> "Lembrete WiFi"
            Reminder.BLUETOOTH -> "Lembrete Bluetooth"
            Reminder.TIME -> "Lembrete agendado"
            Reminder.LOCATION -> "Lembrete de Localização"
            Reminder.SIMPLE -> "Lembrete"
            else -> ""
        }
    }

}