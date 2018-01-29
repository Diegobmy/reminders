package com.ragabuza.personalreminder.util

import android.content.Context
import com.ragabuza.personalreminder.dao.ReminderDAO
import java.util.HashSet

/**
 * Created by diego.moyses on 1/25/2018.
 */
class Shared(val context: Context) {
    private val preferences = context.getSharedPreferences("reminders", 0)
    private val editor = preferences.edit()

//    fun getOldWifi(): MutableSet<String> {
//        return preferences.getStringSet("oldWifi", HashSet<String>())
//    }
//
//    fun setOldWifi(newWifi: MutableSet<String>?) {
//        editor?.putStringSet("oldWifi", newWifi)
//        editor?.commit()
//    }
//
//    fun getCheckedWifi(): MutableSet<String> {
//        return preferences.getStringSet("checkedWifi", HashSet<String>())
//    }
//
//    fun addToCheckedWifi(wifi: String) {
//        val check = preferences.getStringSet("checkedWifi", HashSet<String>())
//        check.add(wifi)
//        editor.putStringSet("checkedWifi", check)
//        editor.commit()
//    }
//
//    fun refreshCheckedWifi(wifi: String) {
//        val check = preferences.getStringSet("checkedWifi", HashSet<String>())
//        val dao = ReminderDAO(context)
//        if (dao.count(wifi) < 1)
//            check.remove(wifi)
//        editor.putStringSet("checkedWifi", check)
//        editor.commit()
//        dao.close()
//    }
}