package com.ragabuza.personalreminder.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import com.ragabuza.personalreminder.model.Reminder
import java.util.ArrayList
import java.util.HashSet

//val id: Long
//var active: Boolean
//val reminder: String
//val type: ReminderType
//val rWhen: ReminderWhen
//val rWhat: ReminderWhat
//val condition: Any
//val extra: String = ""

class ReminderDAO(context: Context?) : SQLiteOpenHelper(context, "Reminder", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val sql = "CREATE TABLE Reminder (" +
                "id INTEGER PRIMARY KEY, " +
                "active INTEGER, " +
                "done TEXT, " +
                "reminder TEXT, " +
                "type TEXT, " +
                "rWhen TEXT, " +
                "condition TEXT, " +
                "extra TEXT);"
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        var sql = ""
//        toSave (oldVersion) {
//            1 -> {
//                sql = "ALTER TABLE Alunos ADD COLUMN caminhoFoto TEXT"
//                db.execSQL(sql) // indo para versao 2
//            }
//        }

    }

    fun add(reminder: Reminder): Long {
        val db = writableDatabase

        val dados = getInfo(reminder)

        return db.insert("Reminder", null, dados)
    }

    private fun getInfo(reminder: Reminder): ContentValues {
        val dados = ContentValues()
        dados.put("active", reminder.active)
        dados.put("done", reminder.done)
        dados.put("reminder", reminder.reminder)
        dados.put("type", reminder.type)
        dados.put("rWhen", reminder.rWhen)
        dados.put("condition", reminder.condition)
        dados.put("extra", reminder.extra)

        return dados
    }

        fun get(): List<Reminder> {
            val sql = "SELECT * FROM Reminder where active=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)

        val reminders = ArrayList<Reminder>()

        while (c.moveToNext()) {
            val reminder = Reminder(
                    c.getLong(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("done")),
                    c.getInt(c.getColumnIndex("active")) == 1,
                    c.getString(c.getColumnIndex("reminder")),
                    c.getString(c.getColumnIndex("type")),
                    c.getString(c.getColumnIndex("rWhen")),
                    c.getString(c.getColumnIndex("condition")),
                    c.getString(c.getColumnIndex("extra"))
            )

            reminders.add(reminder)
        }
        c.close()

        return reminders
    }

    fun getOld(): List<Reminder> {
        val sql = "SELECT * FROM Reminder where active=0;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)

        val reminders = ArrayList<Reminder>()

        while (c.moveToNext()) {
            val reminder = Reminder(
                    c.getLong(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("done")),
                    c.getInt(c.getColumnIndex("active")) == 1,
                    c.getString(c.getColumnIndex("reminder")),
                    c.getString(c.getColumnIndex("type")),
                    c.getString(c.getColumnIndex("rWhen")),
                    c.getString(c.getColumnIndex("condition")),
                    c.getString(c.getColumnIndex("extra"))
            )

            reminders.add(reminder)
        }
        c.close()

        return reminders
    }

    fun getActive(string: String, connected: String?): List<Reminder> {
        val sql = if (connected != null)
            "SELECT * FROM Reminder where condition='$string' and rWhen='$connected' and active=1;"
        else
            "SELECT * FROM Reminder where condition='$string' and active=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val reminders = ArrayList<Reminder>()

        while (c.moveToNext()) {
            val reminder = Reminder(
                    c.getLong(c.getColumnIndex("id")),
                    "",
                    c.getInt(c.getColumnIndex("active")) == 1,
                    c.getString(c.getColumnIndex("reminder")),
                    c.getString(c.getColumnIndex("type")),
                    c.getString(c.getColumnIndex("rWhen")),
                    c.getString(c.getColumnIndex("condition")),
                    c.getString(c.getColumnIndex("extra"))
            )

            reminders.add(reminder)
        }
        c.close()

        return reminders
    }

    fun del(reminder: Reminder) {
        val db = writableDatabase
        val params = arrayOf<String>(reminder.id.toString())
        db.delete("Reminder", "id = ?", params)
    }

    fun alt(reminder: Reminder) {
        val db = writableDatabase

        val dados = getInfo(reminder)

        val params = arrayOf<String>(reminder.id.toString())
        db.update("Reminder", dados, "id = ?", params)
    }

    fun getUniqueWifi(): HashSet<String> {
        val sql = "SELECT * FROM Reminder where type='${Reminder.WIFI}' and active=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val wifi = HashSet<String>()

        while (c.moveToNext()) {
            wifi.add(c.getString(c.getColumnIndex("condition")))
        }
        c.close()
        return wifi
    }
    fun getLocations(): HashSet<Location> {
        val sql = "SELECT * FROM Reminder where type='${Reminder.LOCATION}' and active=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val locations = HashSet<Location>()

        while (c.moveToNext()) {
            val loc = Location("db")
            loc.latitude = c.getString(c.getColumnIndex("condition")).split(",")[0].toDouble()
            loc.longitude = c.getString(c.getColumnIndex("condition")).split(",")[1].toDouble()
            locations.add(loc)
        }
        c.close()
        return locations
    }
    fun getOne(id: Long): Reminder?{
        val sql = "SELECT * FROM Reminder where id='$id' and active=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        var reminder: Reminder? = null

        while (c.moveToNext()) {
            reminder = Reminder(
                    c.getLong(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("done")),
                    c.getInt(c.getColumnIndex("active")) == 1,
                    c.getString(c.getColumnIndex("reminder")),
                    c.getString(c.getColumnIndex("type")),
                    c.getString(c.getColumnIndex("rWhen")),
                    c.getString(c.getColumnIndex("condition")),
                    c.getString(c.getColumnIndex("extra"))
            )
        }
        c.close()
        return reminder
    }
}
