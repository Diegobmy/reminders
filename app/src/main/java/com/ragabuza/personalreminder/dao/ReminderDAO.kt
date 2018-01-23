package com.ragabuza.personalreminder.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.model.ReminderType
import com.ragabuza.personalreminder.model.ReminderWhen
import java.util.ArrayList

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
                "reminder TEXT, " +
                "type INTEGER, " +
                "rWhen INTEGER, " +
                "condition TEXT, " +
                "extra TEXT);"
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        var sql = ""
//        when (oldVersion) {
//            1 -> {
//                sql = "ALTER TABLE Alunos ADD COLUMN caminhoFoto TEXT"
//                db.execSQL(sql) // indo para versao 2
//            }
//        }

    }

    fun add(reminder: Reminder) {
        val db = writableDatabase

        val dados = getInfo(reminder)

        db.insert("Reminder", null, dados)
    }

    private fun getInfo(reminder: Reminder): ContentValues {
        val dados = ContentValues()
        dados.put("active", reminder.active)
        dados.put("reminder", reminder.reminder)
        dados.put("type", reminder.type.ordinal)
        dados.put("rWhen", reminder.rWhen.ordinal)
        dados.put("condition", reminder.condition)
        dados.put("extra", reminder.extra)

        return dados
    }

        fun get(): List<Reminder> {
            val sql = "SELECT * FROM Reminder;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)

        val reminders = ArrayList<Reminder>()

        while (c.moveToNext()) {
            val reminder = Reminder(
                    c.getLong(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("active")) == 1,
                    c.getString(c.getColumnIndex("reminder")),
                    ReminderType.values()[c.getInt(c.getColumnIndex("type"))],
                    ReminderWhen.values()[c.getInt(c.getColumnIndex("rWhen"))],
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
}
