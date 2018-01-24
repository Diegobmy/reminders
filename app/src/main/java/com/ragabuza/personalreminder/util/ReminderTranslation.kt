package com.ragabuza.personalreminder.util

import android.content.Context
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.ReminderType
import com.ragabuza.personalreminder.model.ReminderWhen

/**
 * Created by diego.moyses on 1/24/2018.
 */
class ReminderTranslation(val context: Context) {
    fun type(string: String): ReminderType {
        return when (string) {
            "WIFI" -> ReminderType.WIFI
            "BLUETOOTH" -> ReminderType.BLUETOOTH
            "TIME" -> ReminderType.TIME
            "LOCATION" -> ReminderType.LOCATION
            "SIMPLE" -> ReminderType.SIMPLE
            else -> ReminderType.SIMPLE
        }
    }
    fun `when`(string: String): ReminderWhen{
        return when(string){
            context.getString(R.string.when_is) -> ReminderWhen.IS
            context.getString(R.string.when_isnot) -> ReminderWhen.ISNOT
            context.getString(R.string.when_is_2) -> ReminderWhen.IS
            context.getString(R.string.when_isnot_2) -> ReminderWhen.ISNOT
            else-> ReminderWhen.IS
        }

    }
}