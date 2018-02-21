package com.ragabuza.personalreminder.util

import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.ThemeColor
import java.util.*

/**
 * Created by diego.moyses on 2/6/2018.
 */
class Constants {
    class Intents {
        companion object {
            val IS_TUTORIAL = "tutorial"

            val IS_OUT = "isOlt"
            val REMINDER = "Reminder"
            val KILL_IT = "KillIt"
            val SET_DONE = "setDone"
            val PRIVATE = "Private"
            val PRIVATE_THEME = R.style.AppThemePrivate
            val PRIVATE_THEME_TRANSPARENT = R.style.AppThemePrivate_Transparent
        }
    }

    class ReminderFields {
        companion object {
            val TABLE_NAME = "ReminderTable"
            val P_TABLE_NAME = "PReminderTable"
            val FIELD_ID = "id"
            val FIELD_DONE = "done"
            val FIELD_ACTIVE = "active"
            val FIELD_REMINDER = "reminder"
            val FIELD_TYPE = "type"
            val FIELD_WHEN = "rWhen"
            val FIELD_CONDITION = "condition"
            val FIELD_EXTRA = "extra"
            val FIELD_FOLDER = "folder"
        }
    }

    class Other {
        companion object {
            val WAITING = "WAITING"
            val CONTACT_PREFIX = "CONTACT:"
            val PRIVATE_FOLDER = "PrivateFolder&"
            val EMPTY_FOLDER = "&empty&folder"
        }
    }


    class TimeConstants {
        companion object {
            fun getMinimalDateToday(): Long {
                val cal = Calendar.getInstance()
                return cal.timeInMillis
            }

            fun getMinimalDateTomorrow(): Long {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, 1)
                return cal.timeInMillis
            }
        }
    }
}