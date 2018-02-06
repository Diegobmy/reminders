package com.ragabuza.personalreminder.util

/**
 * Created by diego.moyses on 2/6/2018.
 */
class Constants {
    class Intents{
        companion object {
            val IS_OUT = "isOlt"
            val REMINDER = "Reminder"
            val KILL_IT = "KillIt"
            val SET_DONE = "setDone"
        }
    }
    class ReminderFields{
        companion object {
            val TABLE_NAME = "ReminderTable"
            val FIELD_ID = "id"
            val FIELD_DONE = "done"
            val FIELD_ACTIVE = "active"
            val FIELD_REMINDER = "reminder"
            val FIELD_TYPE = "type"
            val FIELD_WHEN = "rWhen"
            val FIELD_CONDITION = "condition"
            val FIELD_EXTRA = "extra"
        }
    }
    class Other{
        companion object {
            val WAITING = "WAITING"
            val CONTACT_PREFIX = "CONTACT:"
        }
    }
}