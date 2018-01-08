package com.ragabuza.personalreminder.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipboardManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import android.net.ConnectivityManager
import android.widget.Toast


class ReminderList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)

        fabMenu.setClosedOnTouchOutside(true)

        setupDrawer()

        this.supportActionBar!!.title = getString(R.string.remindersActivityTitle)
//        this.supportActionBar?.setDisplayUseLogoEnabled(true)
//        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.bitcoin_clock)

        val reminders = listOf(
                Reminder(1, "1", ReminderType.BLUETOOTH, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(2, "2", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(3, "3", ReminderType.LOCATION, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(4, "4", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(5, "5", ReminderType.BLUETOOTH, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(6, "6", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(7, "7", ReminderType.LOCATION, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(8, "8", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(9, "9", ReminderType.BLUETOOTH, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(10, "10", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(11, "11", ReminderType.LOCATION, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(12, "12", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a")
        )
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mutAlarms = reminders.toMutableList()
        val adapter = ReminderAdapter(this, mutAlarms, connManager)
        lvRemind.adapter = adapter

        fabMenu.setOnClickListener { adapter.closeAllItems() }
        lvRemind.setOnItemClickListener { _, _, _, _ ->
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


            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, _, _, _ -> }, year, month, day)

            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, _, _ -> datePicker.show() },hour, minute, true)

            timePicker.show()

        }
        fabLocation.setOnClickListener {
//            val PLACE_PICKER_REQUEST = 1
//            val builder = PlacePicker.IntentBuilder()
//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }

    }

    fun setupDrawer(){
//        navigation_view.setNavigationItemSelectedListener { menuItem ->
//            Toast.makeText(applicationContext, "${menuItem.itemId}${menuItem.title}", Toast.LENGTH_SHORT).show()
//
//            menuItem.isChecked = true
//            drawer_layout.closeDrawers()
//            true
//        }
    }

    override fun onResume(){
        super.onResume()
        clipShow()
    }

    private fun clipShow(){

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

            val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

            if (clip.toString().matches(regex))
                btClipboard.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
            else
                btClipboard.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            btClipboard.visibility = View.GONE
        }
    }

}
