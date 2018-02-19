package com.ragabuza.personalreminder.util

import android.content.Context
import android.widget.Toast
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
    val PASSWORD = "Password"
    val FINGERPRINT = "Fingerprint"
    val POWER_SAVE = "PowerSave"
    val SHOW_NOTIFICATION = "ShowNotification"
    val FOLDER = "Folder"

    fun hasDeleted(): Boolean {
        return preferences.getBoolean(HAS_DELETED, false)
    }

    private fun setHasDeleted(status: Boolean) {
        editor.putBoolean(HAS_DELETED, status)
        editor.apply()
    }

    fun setLastDeleted(reminder: Reminder?) {
        if (reminder == null)
            setHasDeleted(false)
        else {
            val Gson = Gson()
            val regStr = Gson.toJson(reminder)
            setHasDeleted(true)
            editor.putString(LAST_DELETED, regStr)
            editor.apply()
        }
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

    fun hasFavorites(): Boolean {
        return getFavorites().isNotEmpty()
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
            ThemeColor(0, "Roxo(Padrão)", R.style.AppTheme, R.style.Theme_Transparent, R.color.colorPrimaryDarker, R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimaryLight)
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

    fun setPassword(password: String){
        editor.putString(PASSWORD, password)
        editor.apply()
    }
    fun passwordIsCorrect(password: String):Boolean{
        return password == preferences.getString(PASSWORD, "")
    }
    fun getPassword():String{
        return preferences.getString(PASSWORD, "")
    }
    fun hasPassword():Boolean{
        return "" != preferences.getString(PASSWORD, "")
    }

    fun setFingerprint(status: Boolean){
        editor.putBoolean(FINGERPRINT, status)
        editor.apply()
    }
    fun hasFingerprint():Boolean{
        return preferences.getBoolean(FINGERPRINT, false)
    }

    fun setPowerSave(status: Boolean){
        editor.putBoolean(POWER_SAVE, status)
        editor.apply()
    }
    fun isPowerSave():Boolean{
        return preferences.getBoolean(POWER_SAVE, false)
    }

    fun setShowNotification(status: Boolean){
        editor.putBoolean(SHOW_NOTIFICATION, status)
        editor.apply()
    }
    fun isShowNotification():Boolean{
        return preferences.getBoolean(SHOW_NOTIFICATION, false)
    }

    fun getFolders(): HashSet<String>{
        return preferences.getStringSet(FOLDER, HashSet<String>()) as HashSet<String>
    }
    fun setFolders(folders: HashSet<String>){
        editor.remove(FOLDER)
        editor.apply()
        editor.putStringSet(FOLDER, folders)
        editor.apply()
    }
}