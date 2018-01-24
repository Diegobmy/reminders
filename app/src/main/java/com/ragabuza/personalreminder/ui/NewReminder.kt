package com.ragabuza.personalreminder.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.TimeString
import com.ragabuza.personalreminder.util.ReminderTranslation
import kotlinx.android.synthetic.main.activity_reminder.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by diego.moyses on 1/12/2018.
 */
class NewReminder : AppCompatActivity(), OpDialogInterface {

    var cond: String = ""

    override fun contactCall(text: CharSequence) {
        etExtra.setText(text)
    }

    override fun other(text: CharSequence) {
        etConditionExtra.setText(text)
    }

    override fun timeCall(date: Calendar) {
        cond = date.time.toString()
        etCondition.setText(TimeString(date).getString(false))
    }

    override fun wifiCall(text: CharSequence) {
        cond = text.toString()
        etCondition.setText(text)
    }

    override fun blueCall(text: CharSequence) {
        cond = text.toString()
        etCondition.setText(text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        etReminder.requestFocus()

        val extras = intent.extras

        val condition = extras.getString("condition")
        val type = extras.getString("type")

        if (condition != null)
            if (type == "TIME") {
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
                cal.time = sdf.parse(condition)
                etCondition.setText(TimeString(cal).getString(false))
                cond = condition
            } else
            etCondition.setText(condition)


        hintCondition.hint = when (type) {
            "WIFI" -> "Rede WiFi"
            "BLUETOOTH" -> "Dispositivo Bluetooth"
            "LOCATION" -> "Local"
            "TIME" -> "Horário"
            else -> "Condição"
        }

        if (type == "SIMPLE") {
            etCondition.visibility = View.GONE
            etConditionExtra.visibility = View.GONE
        } else if (type == "TIME") {
            etConditionExtra.visibility = View.GONE
        }

        etCondition.keyListener = null

        etCondition.setOnFocusChangeListener { _, b ->
            if (b) when (type) {
                "WIFI" -> DialogAdapter(this, "W").show()
                "BLUETOOTH" -> DialogAdapter(this, "B").show()
                "TIME" -> DialogAdapter(this, "T").show()
            }
        }
        etCondition.setOnClickListener {
            when (type) {
                "WIFI" -> DialogAdapter(this, "W").show()
                "BLUETOOTH" -> DialogAdapter(this, "B").show()
                "TIME" -> DialogAdapter(this, "T").show()
            }
        }

        etConditionExtra.keyListener = null
        etConditionExtra.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, "OWEB").show()
        }
        etConditionExtra.setOnClickListener {
            DialogAdapter(this, "OWEB").show()
        }


        ibContacts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 0)
            } else
                DialogAdapter(this, "CON").show()
        }

        btn_cancel.setOnClickListener { finish() }
        btn_save.setOnClickListener {
            val DAO = ReminderDAO(this)
            val translator = ReminderTranslation(this)
            DAO.add(Reminder(
                    1,
                    true,
                    etReminder.text.toString(),
                    translator.type(type),
                    translator.`when`(etConditionExtra.text.toString()),
                    cond,
                    etExtra.text.toString()
            ))
            DAO.close()
            val intent = Intent(this, ReminderList::class.java)
            startActivity(intent)
        }

        val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
        etExtra.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().matches(regex))
                    etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
                else
                    etExtra.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

}