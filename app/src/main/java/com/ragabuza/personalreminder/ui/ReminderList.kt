package com.ragabuza.personalreminder.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipboardManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import com.ragabuza.personalreminder.adapter.ReminderAdapter
import com.ragabuza.personalreminder.model.*
import kotlinx.android.synthetic.main.activity_reminder_list.*
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import java.util.*
import android.view.View
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan


class ReminderList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)

        fabMenu.setClosedOnTouchOutside(true)


        this.supportActionBar!!.title = "Lembretes"
//        this.supportActionBar?.setDisplayUseLogoEnabled(true)
//        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.bitcoin_clock)

        clipShow()

        val reminders = listOf<Reminder>(
                Reminder(1, "1", ReminderType.BLUETOOTH, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(2, "2", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(3, "3", ReminderType.LOCATION, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(4, "4", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a")
        )

        val mutAlarms = reminders.toMutableList()
        val adapter = ReminderAdapter(this, mutAlarms)
        lvRemind.adapter = adapter

        fabMenu.setOnClickListener { adapter.closeAllItems() }
        lvRemind.setOnItemClickListener { adapterView, view, i, l ->
            adapter.closeAllItems()
        }

        fabBluetooth.setOnClickListener {
            DialogAdapter(this, "B").show()
        }
        fabWifi.setOnClickListener {
            DialogAdapter(this, "W").show()
        }
        fabTime.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)

            val day = mcurrentTime.get(Calendar.DAY_OF_MONTH)
            val month = mcurrentTime.get(Calendar.MONTH)
            val year = mcurrentTime.get(Calendar.YEAR)


            val datepicker = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                }
            }, year, month, day)

            val timepicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                    datepicker.show()
                }
            },hour, minute, true)

            timepicker.show()

        }
        fabLocation.setOnClickListener {
//            val PLACE_PICKER_REQUEST = 1
//            val builder = PlacePicker.IntentBuilder()
//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }

    }

    override fun onResume(){
        super.onResume()
        clipShow()
    }

    fun clipShow(){

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        var clip = clipboard.primaryClip.getItemAt(0).text

        if (!clipboard.primaryClip.getItemAt(0).text.isEmpty() && clipboard.primaryClip.getItemAt(0).text.toString() != sharedPref.getString("clip", "")) {
            editor.putString("clip" ,clip.toString())
            editor.apply()
            btClipboard.visibility = View.VISIBLE
            clip = clipboard.primaryClip.getItemAt(0).text
            if (clip.length > 150) {
                val spannable = SpannableString("${getString(R.string.clipboard)}\n\"${clip.subSequence(0, 150)}...\"")
                spannable.setSpan(StyleSpan(Typeface.ITALIC), getString(R.string.clipboard).length,spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                btClipboard.text = spannable
            }
            else {
                val spannable = SpannableString("${getString(R.string.clipboard)}\n\"$clip\"")
                spannable.setSpan(StyleSpan(Typeface.ITALIC), getString(R.string.clipboard).length,spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                btClipboard.text = spannable
            }
        } else {
            btClipboard.visibility = View.GONE
        }
    }

}
