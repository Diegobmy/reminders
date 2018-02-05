package com.ragabuza.personalreminder.ui

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
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
import kotlinx.android.synthetic.main.activity_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import android.text.style.ImageSpan
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.google.android.gms.location.places.ui.PlacePicker
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.ragabuza.personalreminder.receivers.TimeReceiver
import com.ragabuza.personalreminder.util.*


/**
 * Created by diego.moyses on 1/12/2018.
 */
class NewReminder : AppCompatActivity(), OpDialogInterface {

    val PLACE_PICKER_REQUEST = 1

    override fun closed(tag: String?) {}

    var ID: Long = 0
    var cond: String = ""
    var contact = false
    private val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

    private lateinit var preferences: Shared

    override fun contactCall(text: CharSequence, tag: String?) {
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

    override fun other(text: CharSequence, tag: String?) {
        etConditionExtra.setText(text)
    }

    override fun timeCall(date: Calendar, tag: String?) {
        cond = date.time.toString()
        etCondition.setText(TimeString(date).getString(false))
    }

    override fun wifiCall(text: CharSequence, tag: String?) {
        cond = text.toString()
        etCondition.setText(text)
    }

    override fun blueCall(text: CharSequence, tag: String?) {
        cond = text.toString()
        etCondition.setText(text)
    }

    private var mapWidth: Int = 400
    private var mapHeight: Int = 200

    private var edition: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        preferences = Shared(this)
        etReminder.requestFocus()

        val extras = intent.extras
        cond = extras.getString("condition", "")
        var type = extras.getString("type", "")

        supportActionBar?.title = ReminderTranslation(this).reminderType(type)

        etExtra.setText(extras.getString("shareExtra", ""))
        etReminder.setText(extras.getString("shareText", ""))
        if (extras.getString("shareExtra", "").matches(regex))
            etExtra.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)

        etConditionExtra.setText(getString(R.string.when_is))

        if (extras.getBoolean("isOut", false))
            etConditionExtra.setText(getString(R.string.when_isnot))

        val reminderEdited = extras.getParcelable<Reminder>("Reminder")
        if (reminderEdited != null) {
            edition = true
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
        } else if (type == Reminder.LOCATION) {

        } else
            etCondition.setText(cond)


        hintCondition.hint = when (type) {
            Reminder.WIFI -> "Rede WiFi"
            Reminder.BLUETOOTH -> "Dispositivo Bluetooth"
            Reminder.LOCATION -> "Local"
            Reminder.TIME -> "Horário"
            else -> "Condição"
        }

        when (type) {
            Reminder.SIMPLE -> {
                etCondition.visibility = View.GONE
                etConditionExtra.visibility = View.GONE
            }
            Reminder.TIME -> {
                etConditionExtra.visibility = View.GONE
            }
            Reminder.LOCATION -> {
                etCondition.visibility = View.GONE
                etConditionExtra.visibility = View.GONE
                mapView.visibility = View.VISIBLE
                getMap()
            }
        }

        if (type != Reminder.LOCATION) {
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
        } else {
            mapView.setOnClickListener {
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
            }
        }
        etConditionExtra.keyListener = null
        etConditionExtra.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, this, DialogAdapter.CHOICE_WEB).show()
        }
        etConditionExtra.setOnClickListener {
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
            val rWhen: String = if (type == Reminder.LOCATION) {
                ReminderTranslation(this).locationAddress(cond)
            } else
                ReminderTranslation(this).toSave(etConditionExtra.text.toString())
            val reminder = Reminder(
                    ID,
                    "",
                    true,
                    etReminder.text.toString(),
                    type,
                    rWhen,
                    cond,
                    if (contact) "CONTACT:${etExtra.text}" else etExtra.text.toString()
            )
            val dao = ReminderDAO(this)
            if (ID == 0L)
                ID = dao.add(reminder)
            else
                dao.alt(reminder)
            dao.close()
            if (type == Reminder.TIME) setAlarm()
            finish()
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

    private fun setAlarm() {
        val alarm = AlarmHelper(this)
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
        cal.time = sdf.parse(cond)
        if (edition)
            alarm.stopAlarm(ID)
        alarm.setAlarm(ID, cal.timeInMillis)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(data, this)
                locationCall("${place.latLng.latitude},${place.latLng.longitude}")
            }
        }
    }

    private fun locationCall(location: String) {
        cond = location
        getMap()
    }

    private fun getMap() {
        val request = "https://maps.googleapis.com/maps/api/staticmap?markers=$cond&size=${mapWidth}x$mapHeight&key=${getString(R.string.google_maps_api)}"
        DownloadImage(mapView).execute(request)
    }

}