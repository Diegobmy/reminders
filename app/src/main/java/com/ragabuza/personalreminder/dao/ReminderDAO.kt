package com.ragabuza.personalreminder.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.Constants.Other.Companion.PRIVATE_FOLDER
import com.ragabuza.personalreminder.util.Constants.Other.Companion.WAITING
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_ACTIVE
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_CONDITION
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_DONE
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_EXTRA
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_FOLDER
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_ID
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_REMINDER
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_TYPE
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_WHEN
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.TABLE_NAME
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

class ReminderDAO(context: Context?) : SQLiteOpenHelper(context, TABLE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val sql = "CREATE TABLE $TABLE_NAME (" +
                "$FIELD_ID INTEGER PRIMARY KEY, " +
                "$FIELD_ACTIVE INTEGER, " +
                "$FIELD_DONE TEXT, " +
                "$FIELD_REMINDER TEXT, " +
                "$FIELD_TYPE TEXT, " +
                "$FIELD_WHEN TEXT, " +
                "$FIELD_CONDITION TEXT, " +
                "$FIELD_EXTRA TEXT, " +
                "$FIELD_FOLDER TEXT);"
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

        return db.insert(TABLE_NAME, null, dados)
    }

    private fun getInfo(reminder: Reminder): ContentValues {
        val dados = ContentValues()
        dados.put(FIELD_ACTIVE, reminder.active)
        dados.put(FIELD_DONE, reminder.done)
        dados.put(FIELD_REMINDER, reminder.reminder)
        dados.put(FIELD_TYPE, reminder.type)
        dados.put(FIELD_WHEN, reminder.rWhen)
        dados.put(FIELD_CONDITION, reminder.condition)
        dados.put(FIELD_EXTRA, reminder.extra)
        dados.put(FIELD_FOLDER, reminder.folder)

        return dados
    }

    fun get(): List<Reminder> {
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_ACTIVE=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)

        val reminders = ArrayList<Reminder>()

            while (c.moveToNext()) {
                val reminder = Reminder(
                        c.getLong(c.getColumnIndex(FIELD_ID)),
                        c.getString(c.getColumnIndex(FIELD_DONE)),
                        c.getInt(c.getColumnIndex(FIELD_ACTIVE)) == 1,
                        c.getString(c.getColumnIndex(FIELD_REMINDER)),
                        c.getString(c.getColumnIndex(FIELD_TYPE)),
                        c.getString(c.getColumnIndex(FIELD_WHEN)),
                        c.getString(c.getColumnIndex(FIELD_CONDITION)),
                        c.getString(c.getColumnIndex(FIELD_EXTRA)),
                        c.getString(c.getColumnIndex(FIELD_FOLDER))
                )
                reminders.add(reminder)
            }
            c.close()

        return reminders
    }

    fun countNew(private: Boolean = false): Int {
        val cond = if (private) "==" else "!="
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_ACTIVE=1 and $FIELD_FOLDER$cond'$PRIVATE_FOLDER';"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val count = c.count
        c.close()
        return count
    }

    fun countOld(private: Boolean = false): Int {
        val cond = if (private) "==" else "!="
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_ACTIVE=0 and $FIELD_FOLDER$cond'$PRIVATE_FOLDER';"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val count = c.count
        c.close()
        return count
    }

    fun getOld(): List<Reminder> {
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_ACTIVE=0;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)

        val reminders = ArrayList<Reminder>()

        while (c.moveToNext()) {
            val reminder = Reminder(
                    c.getLong(c.getColumnIndex(FIELD_ID)),
                    c.getString(c.getColumnIndex(FIELD_DONE)),
                    c.getInt(c.getColumnIndex(FIELD_ACTIVE)) == 1,
                    c.getString(c.getColumnIndex(FIELD_REMINDER)),
                    c.getString(c.getColumnIndex(FIELD_TYPE)),
                    c.getString(c.getColumnIndex(FIELD_WHEN)),
                    c.getString(c.getColumnIndex(FIELD_CONDITION)),
                    c.getString(c.getColumnIndex(FIELD_EXTRA)),
                    c.getString(c.getColumnIndex(FIELD_FOLDER))
            )

            reminders.add(reminder)
        }
        c.close()

        return reminders
    }

    fun getWaiting(): List<Reminder> {
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_DONE='$WAITING';"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)

        val reminders = ArrayList<Reminder>()

        while (c.moveToNext()) {
            val reminder = Reminder(
                    c.getLong(c.getColumnIndex(FIELD_ID)),
                    c.getString(c.getColumnIndex(FIELD_DONE)),
                    c.getInt(c.getColumnIndex(FIELD_ACTIVE)) == 1,
                    c.getString(c.getColumnIndex(FIELD_REMINDER)),
                    c.getString(c.getColumnIndex(FIELD_TYPE)),
                    c.getString(c.getColumnIndex(FIELD_WHEN)),
                    c.getString(c.getColumnIndex(FIELD_CONDITION)),
                    c.getString(c.getColumnIndex(FIELD_EXTRA)),
                    c.getString(c.getColumnIndex(FIELD_FOLDER))
            )

            reminders.add(reminder)
        }
        c.close()

        return reminders
    }

    fun getActive(type: String, string: String, connected: String?): List<Reminder> {
        val sql = if (connected != null)
            "SELECT * FROM $TABLE_NAME where $FIELD_TYPE='$type' and $FIELD_CONDITION='$string' and $FIELD_WHEN='$connected' and $FIELD_ACTIVE=1;"
        else
            "SELECT * FROM $TABLE_NAME where $FIELD_CONDITION='$string' and $FIELD_ACTIVE=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val reminders = ArrayList<Reminder>()

        while (c.moveToNext()) {
            val reminder = Reminder(
                    c.getLong(c.getColumnIndex(FIELD_ID)),
                    "",
                    c.getInt(c.getColumnIndex(FIELD_ACTIVE)) == 1,
                    c.getString(c.getColumnIndex(FIELD_REMINDER)),
                    c.getString(c.getColumnIndex(FIELD_TYPE)),
                    c.getString(c.getColumnIndex(FIELD_WHEN)),
                    c.getString(c.getColumnIndex(FIELD_CONDITION)),
                    c.getString(c.getColumnIndex(FIELD_EXTRA)),
                    c.getString(c.getColumnIndex(FIELD_FOLDER))
            )

            reminders.add(reminder)
        }
        c.close()

        return reminders
    }

    fun del(reminder: Reminder) {
        val db = writableDatabase
        val params = arrayOf<String>(reminder.id.toString())
        db.delete(TABLE_NAME, "$FIELD_ID = ?", params)
    }

    fun alt(reminder: Reminder) {
        val db = writableDatabase

        val dados = getInfo(reminder)

        val params = arrayOf<String>(reminder.id.toString())
        db.update(TABLE_NAME, dados, "$FIELD_ID = ?", params)
    }

    fun getUniqueWifi(): HashSet<String> {
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_TYPE='${Reminder.WIFI}' and $FIELD_ACTIVE=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val wifi = HashSet<String>()

        while (c.moveToNext()) {
            wifi.add(c.getString(c.getColumnIndex(FIELD_CONDITION)))
        }
        c.close()
        return wifi
    }

    fun getLocations(): HashSet<Location> {
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_TYPE='${Reminder.LOCATION}' and $FIELD_ACTIVE=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        val locations = HashSet<Location>()

        while (c.moveToNext()) {
            val loc = Location("db")
            loc.latitude = c.getString(c.getColumnIndex(FIELD_CONDITION)).split(",")[0].toDouble()
            loc.longitude = c.getString(c.getColumnIndex(FIELD_CONDITION)).split(",")[1].toDouble()
            locations.add(loc)
        }
        c.close()
        return locations
    }

    fun getOne(id: Long): Reminder? {
        val sql = "SELECT * FROM $TABLE_NAME where $FIELD_ID='$id' and $FIELD_ACTIVE=1;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)
        var reminder: Reminder? = null

        while (c.moveToNext()) {
            reminder = Reminder(
                    c.getLong(c.getColumnIndex(FIELD_ID)),
                    c.getString(c.getColumnIndex(FIELD_DONE)),
                    c.getInt(c.getColumnIndex(FIELD_ACTIVE)) == 1,
                    c.getString(c.getColumnIndex(FIELD_REMINDER)),
                    c.getString(c.getColumnIndex(FIELD_TYPE)),
                    c.getString(c.getColumnIndex(FIELD_WHEN)),
                    c.getString(c.getColumnIndex(FIELD_CONDITION)),
                    c.getString(c.getColumnIndex(FIELD_EXTRA)),
                    c.getString(c.getColumnIndex(FIELD_FOLDER))
            )
        }
        c.close()
        return reminder
    }

    fun removeEverything(): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "1", null)
    }
}
