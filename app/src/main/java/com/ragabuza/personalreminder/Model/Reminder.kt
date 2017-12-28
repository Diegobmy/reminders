package com.ragabuza.personalreminder.Model

/**
 * Created by diego.moyses on 12/28/2017.
 */

enum class ReminderType {WIFI, BLUETOOTH, LOCATION, TIME}
enum class ReminderWhen {GOT, LEAVE}
enum class ReminderWhat {TEXT, URL, CONTACT}

data class Reminder(val id: Long, val reminder: Any, val type: ReminderType , val rWhen: ReminderWhen, val rWhat: ReminderWhat)