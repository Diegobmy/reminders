package com.ragabuza.personalreminder.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.Typeface.ITALIC
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.TimeString
import com.ragabuza.personalreminder.util.ReminderTranslation
import com.ragabuza.personalreminder.util.Shared
import kotlinx.android.synthetic.main.activity_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import android.text.style.ImageSpan
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan


/**
 * Created by diego.moyses on 1/12/2018.
 */
class NewReminder : AppCompatActivity(), OpDialogInterface {

    var cond: String = ""
    var contact = false

    private lateinit var preferences: Shared

    override fun contactCall(text: CharSequence) {
        contact = false
        val ss = SpannableString(" $text")
        val d = resources.getDrawable(R.drawable.ic_contact)
        d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
        val span = ImageSpan(d, ImageSpan.ALIGN_BOTTOM)
        ss.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan( StyleSpan(ITALIC), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan( ForegroundColorSpan(resources.getColor(R.color.contactColor)), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        etExtra.setText(ss)
        etExtra.setSelection(ss.length)
        etExtra.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        contact = true
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

        preferences = Shared(this)

        etReminder.requestFocus()

        val extras = intent.extras
        val condition = extras.getString("condition")
        val type = extras.getString("type")
        cond = condition

        if (type == "LOCATION")
            etConditionExtra.setText(getString(R.string.when_is_2))
        else
            etConditionExtra.setText(getString(R.string.when_is))

        if (condition != null)
            if (type == "TIME") {
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
                cal.time = sdf.parse(condition)
                etCondition.setText(TimeString(cal).getString(false))
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
                "WIFI" -> DialogAdapter(this, this, "W").show()
                "BLUETOOTH" -> DialogAdapter(this, this, "B").show()
                "TIME" -> DialogAdapter(this, this, "T").show()
            }
        }
        etCondition.setOnClickListener {
            when (type) {
                "WIFI" -> DialogAdapter(this, this, "W").show()
                "BLUETOOTH" -> DialogAdapter(this, this, "B").show()
                "TIME" -> DialogAdapter(this, this, "T").show()
            }
        }

        etConditionExtra.keyListener = null
        etConditionExtra.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, this, "OWEB").show()
        }
        etConditionExtra.setOnClickListener {
            DialogAdapter(this, this, "OWEB").show()
        }


        ibContacts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 0)
            } else
                DialogAdapter(this, this, "CON").show()
        }

        btn_cancel.setOnClickListener { finish() }
        btn_save.setOnClickListener {
            if (etReminder.text.toString() == "") {
                etReminder.error = getString(R.string.please_fill_reminder)
                return@setOnClickListener
            }
            val dao = ReminderDAO(this)
            val translator = ReminderTranslation(this)
            dao.add(Reminder(
                    1,
                    true,
                    etReminder.text.toString(),
                    translator.type(type),
                    translator.`when`(etConditionExtra.text.toString()),
                    cond,
                    if (contact) "CONTACT:${etExtra.text}" else etExtra.text.toString()
            ))
            dao.close()

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

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (contact) {
                    contact = false
                    if (p0 != null) {
                        if(p2 > p3)
                            etExtra.setText("")
                        else
                            etExtra.setText(p0.toString().substring(p0.length - 1 until p0.length))
                    }
                    else
                        etExtra.setText("")
                    etExtra.setSelection(etExtra.text.length)
                    etExtra.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
        })
    }

}