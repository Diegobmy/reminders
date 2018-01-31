package com.ragabuza.personalreminder.util

import android.content.Context
import com.google.gson.Gson
import com.ragabuza.personalreminder.model.Reminder

/**
 * Created by diego.moyses on 1/25/2018.
 */
class Shared(val context: Context) {
    private val preferences = context.getSharedPreferences("reminders", 0)
    private val editor = preferences.edit()

    fun setLastDeleted(reminder: Reminder){
        val Gson = Gson()
        val regStr = Gson.toJson(reminder)
        editor.putString("LastDeleted", regStr)
        editor.apply()
    }
    fun getLastDeleted(): Reminder{
        val Gson = Gson()
        val json = preferences.getString("LastDeleted", "")
        return Gson.fromJson(json, Reminder::class.java)
    }
    fun setHome(condition:String){
        editor.putString("Home", condition)
        editor.apply()
    }
    fun getHome(): String{
        return preferences.getString("Home", "")
    }
    fun setWork(condition:String){
        editor.putString("Work", condition)
        editor.apply()
    }
    fun getWork(): String{
        return preferences.getString("Work", "")
    }
}