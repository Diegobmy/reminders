package com.ragabuza.personalreminder.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class WifiDAO(context: Context?) : SQLiteOpenHelper(context, "OldWifi", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val sql = "CREATE TABLE OldWifi (" +
                "SSID TEXT);"
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

    fun add(list: HashSet<String>) {
        val db = writableDatabase

        db.execSQL("delete from OldWifi")

        list.forEach {
            val dados = ContentValues()
            dados.put("SSID", it)
            db.insert("OldWifi", null, dados)
        }
    }

    fun get(): HashSet<String> {
        val sql = "SELECT * FROM OldWifi;"
        val db = readableDatabase
        val c = db.rawQuery(sql, null)

        val list = HashSet<String>()

        while (c.moveToNext()) {
            list.add(c.getString(c.getColumnIndex("SSID")))
        }
        c.close()

        return list
    }

}
