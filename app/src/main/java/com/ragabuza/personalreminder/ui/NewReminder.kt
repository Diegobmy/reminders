package com.ragabuza.personalreminder.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan


/**
 * Created by diego.moyses on 1/12/2018.
 */
class NewReminder : AppCompatActivity(), OpDialogInterface {
    override fun closed() {    }

    var ID: Long = 1
    var cond: String = ""
    var contact = false
    private val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

    private lateinit var preferences: Shared

    override fun contactCall(text: CharSequence) {
        contact = false
        val ss = SpannableString(" $text")
        val d = resources.getDrawable(R.drawable.ic_contact)
        d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
        val span = ImageSpan(d, ImageSpan.ALIGN_BOTTOM)
        ss.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(ITALIC), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(resources.getColor(R.color.contactColor)), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
        cond = extras.getString("condition", "")
        var type = extras.getString("type", "")

        etExtra.setText(extras.getString("shareExtra", ""))
        etReminder.setText(extras.getString("shareText", ""))
        if (extras.getString("shareExtra", "").matches(regex))
            etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)

        if (type == Reminder.LOCATION)
            etConditionExtra.setText(getString(R.string.when_is_2))
        else
            etConditionExtra.setText(getString(R.string.when_is))

        val reminderEdited = extras.getParcelable<Reminder>("Reminder")
        if (reminderEdited != null) {
            ID = reminderEdited.id
            type = reminderEdited.type
            etCondition.setText(reminderEdited.condition)
            cond = reminderEdited.condition
            etConditionExtra.setText(ReminderTranslation(this).toString(reminderEdited.rWhen, type))
            etReminder.setText(reminderEdited.reminder)
            etExtra.setText(reminderEdited.extra)
        }

        if (type == Reminder.TIME) {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
            cal.time = sdf.parse(cond)
            etCondition.setText(TimeString(cal).getString(false))
        } else
            etCondition.setText(cond)


        hintCondition.hint = when (type) {
            Reminder.WIFI -> "Rede WiFi"
            Reminder.BLUETOOTH -> "Dispositivo Bluetooth"
            Reminder.LOCATION -> "Local"
            Reminder.TIME -> "Horário"
            else -> "Condição"
        }

        if (type == Reminder.SIMPLE) {
            etCondition.visibility = View.GONE
            etConditionExtra.visibility = View.GONE
        } else if (type == Reminder.TIME) {
            etConditionExtra.visibility = View.GONE
        }

        etCondition.keyListener = null

        etCondition.setOnFocusChangeListener { _, b ->
            if (b) when (type) {
                Reminder.WIFI -> DialogAdapter(this, this, DialogAdapter.WIFI).show()
                Reminder.BLUETOOTH -> DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
                Reminder.TIME -> DialogAdapter(this, this, DialogAdapter.TIME).show()
            }
        }
        etCondition.setOnClickListener {
            when (type) {
                Reminder.WIFI -> DialogAdapter(this, this, DialogAdapter.WIFI).show()
                Reminder.BLUETOOTH -> DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
                Reminder.TIME -> DialogAdapter(this, this, DialogAdapter.TIME).show()
            }
        }

        etConditionExtra.keyListener = null
        etConditionExtra.setOnFocusChangeListener { _, b ->
            if (b)
                if (type == Reminder.LOCATION)
                    DialogAdapter(this, this, DialogAdapter.CHOICE_LOCATION).show()
                else
                    DialogAdapter(this, this, DialogAdapter.CHOICE_WEB).show()
        }
        etConditionExtra.setOnClickListener {
            if (type == Reminder.LOCATION)
                DialogAdapter(this, this, DialogAdapter.CHOICE_LOCATION).show()
            else
                DialogAdapter(this, this, DialogAdapter.CHOICE_WEB).show()
        }


        ibContacts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 0)
            } else
                DialogAdapter(this, this, DialogAdapter.CONTACTS).show()
        }

        btn_cancel.setOnClickListener { finish() }
        btn_save.setOnClickListener {
            if (etReminder.text.toString() == "" && etExtra.text.toString() == "") {
                etReminder.error = getString(R.string.please_fill_reminder)
                return@setOnClickListener
            }
            val reminder = Reminder(
                    ID,
                    "",
                    true,
                    etReminder.text.toString(),
                    type,
                    ReminderTranslation(this).toSave(etConditionExtra.text.toString()),
                    cond,
                    if (contact) "CONTACT:${etExtra.text}" else etExtra.text.toString()
            )
            val dao = ReminderDAO(this)
            if (ID == 1L)
                dao.add(reminder)
            else
                dao.alt(reminder)
            dao.close()

            val intent = Intent(this, ReminderList::class.java)
            startActivity(intent)
        }

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
                        if (p2 > p3)
                            etExtra.setText("")
                        else
                            etExtra.setText(p0.toString().substring(p0.length - 1 until p0.length))
                    } else
                        etExtra.setText("")
                    etExtra.setSelection(etExtra.text.length)
                    etExtra.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
        })
    }

}