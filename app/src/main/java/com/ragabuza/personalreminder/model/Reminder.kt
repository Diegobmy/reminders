package com.ragabuza.personalreminder.model

/**
 * Created by diego.moyses on 12/28/2017.
 */

enum class ReminderType { WIFI, BLUETOOTH, LOCATION, TIME, SIMPLE }
enum class ReminderWhen { IS, ISNOT }

data class Reminder(val id: Long, var active: Boolean, val reminder: String = "", val type: ReminderType, val rWhen: ReminderWhen = ReminderWhen.IS, val condition: String = "", val extra: String = "")