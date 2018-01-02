package com.ragabuza.personalreminder.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.support.v4.app.ShareCompat.IntentBuilder



class ReminderList : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)

        this.supportActionBar!!.title = "Lembretes"
//        this.supportActionBar?.setDisplayUseLogoEnabled(true)
//        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.bitcoin_clock)


        val reminders = listOf<Reminder>(
                Reminder(1, "1", ReminderType.BLUETOOTH, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(2, "2", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(3, "3", ReminderType.LOCATION, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a"),
                Reminder(4, "4", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "a", "a")
        )

        val mutAlarms = reminders.toMutableList()
        val adapter = ReminderAdapter(this, mutAlarms)
        lvRemind.adapter = adapter

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




    }
}
