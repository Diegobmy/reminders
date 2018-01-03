package com.ragabuza.personalreminder.model

/**
 * Created by diego.moyses on 12/28/2017.
 */

enum class ReminderType {WIFI, BLUETOOTH, LOCATION, TIME}
enum class ReminderWhen {IS, ISNOT}
enum class ReminderWhat {TEXT, URL, CONTACT}

data class Reminder(val id: Long, val reminder: String, val type: ReminderType , val rWhen: ReminderWhen, val rWhat: ReminderWhat, val condition: Any, val action: Any)