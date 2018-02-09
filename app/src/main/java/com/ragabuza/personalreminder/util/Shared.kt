package com.ragabuza.personalreminder.util

import android.content.Context
import com.google.gson.Gson
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.ThemeColor
import com.ragabuza.personalreminder.model.Favorite
import com.ragabuza.personalreminder.model.Reminder

/**
 * Created by diego.moyses on 1/25/2018.
 */
class Shared(val context: Context) {
    private val preferences = context.getSharedPreferences("reminders", 0)
    private val editor = preferences.edit()

    val HAS_DELETED = "HasDeleted"
    val LAST_DELETED = "LastDeleted"
    val HOME = "Home"
    val WORK = "Work"
    val CLIP = "Clip"
    val FAVORITE = "Favorite"
    val THEME = "Theme"

    fun hasDeleted(): Boolean {
        return preferences.getBoolean(HAS_DELETED, false)
    }

    private fun setHasDeleted(status: Boolean) {
        editor.putBoolean(HAS_DELETED, status)
        editor.apply()
    }

    fun setLastDeleted(reminder: Reminder) {
        val Gson = Gson()
        val regStr = Gson.toJson(reminder)
        setHasDeleted(true)
        editor.putString(LAST_DELETED, regStr)
        editor.apply()
    }

    fun getLastDeleted(): Reminder {
        val Gson = Gson()
        val json = preferences.getString(LAST_DELETED, "")
        setHasDeleted(false)
        return Gson.fromJson(json, Reminder::class.java)
    }

    fun setFavorites(favorites: List<Favorite>) {
        val Gson = Gson()
        val encodedFavorites = HashSet<String>()
        favorites.forEach {
            encodedFavorites.add(Gson.toJson(it))
        }
        editor.putStringSet(FAVORITE, encodedFavorites)
        editor.apply()
    }

    fun getFavorites(): MutableList<Favorite> {
        val Gson = Gson()
        val encodedFavorites = preferences.getStringSet(FAVORITE, HashSet<String>())
        val favorites = mutableListOf<Favorite>()
        encodedFavorites.forEach {
            favorites.add(Gson.fromJson(it, Favorite::class.java))
        }
        return favorites
    }

    fun setTheme(theme: ThemeColor) {
        val Gson = Gson()
        val regStr = Gson.toJson(theme)
        editor.putString(THEME, regStr)
        editor.apply()
    }

    fun getTheme(): ThemeColor {
        val Gson = Gson()
        val json = preferences.getString(THEME, "")
        return if (json.isEmpty())
            ThemeColor(0, R.style.AppTheme, R.color.colorPrimaryDarker, R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimaryLight)
        else
            Gson.fromJson(json, ThemeColor::class.java)
    }

    fun setClip(clip: String) {
        editor.putString(CLIP, clip)
        editor.apply()
    }

    fun getClip(): String {
        return preferences.getString(CLIP, "")
    }


}