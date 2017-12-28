package com.ragabuza.personalreminder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ragabuza.personalreminder.Adapter.ReminderAdapter
import com.ragabuza.personalreminder.Model.*
import kotlinx.android.synthetic.main.activity_reminder_list.*
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.interfaces.SwipeAdapterInterface


class ReminderList : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)

        this.supportActionBar!!.title = "Lembretes"
//        this.supportActionBar?.setDisplayUseLogoEnabled(true)
//        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.bitcoin_clock)


        val reminders = listOf<Reminder>(
                Reminder(1, "1", ReminderType.BLUETOOTH, ReminderWhen.GOT, ReminderWhat.CONTACT),
                Reminder(2, "2", ReminderType.BLUETOOTH, ReminderWhen.GOT, ReminderWhat.CONTACT),
                Reminder(3, "3", ReminderType.BLUETOOTH, ReminderWhen.GOT, ReminderWhat.CONTACT),
                Reminder(4, "4", ReminderType.BLUETOOTH, ReminderWhen.GOT, ReminderWhat.CONTACT),
                Reminder(5, "5", ReminderType.BLUETOOTH, ReminderWhen.GOT, ReminderWhat.CONTACT),
                Reminder(6, "6", ReminderType.BLUETOOTH, ReminderWhen.GOT, ReminderWhat.CONTACT)
        )

        val MutAlarms = reminders.toMutableList()
        val adapter = ReminderAdapter(this, MutAlarms)
        lvRemind.adapter = adapter


        lvRemind.setOnItemClickListener { adapterView, view, i, l ->
            adapter.closeAllItems()
        }

    }

    override fun onResume(){
        super.onResume()




    }
}
