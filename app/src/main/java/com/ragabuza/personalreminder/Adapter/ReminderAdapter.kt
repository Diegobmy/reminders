package com.ragabuza.personalreminder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.ReminderType


/**
 * Created by diego.moyses on 12/28/2017.
 */
class ReminderAdapter(private val context: Context, private val reminders: MutableList<Reminder>): BaseSwipeAdapter() {
    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.slReminders
    }

    override fun generateView(position: Int, parent: ViewGroup?): View {

        val inflater = LayoutInflater.from(context)

        var view = inflater.inflate(R.layout.list_item, parent, false)

        return view
    }

    override fun fillValues(position: Int, convertView: View?) {

        val reminder: Reminder = reminders[position]

        convertView?.findViewById<TextView>(R.id.tvName)?.text = reminder.reminder

        val iconElement = convertView?.findViewById<ImageView>(R.id.ivIcon)
        when(reminder.type){
            ReminderType.WIFI -> iconElement?.setImageResource(R.drawable.ic_wifi)
            ReminderType.BLUETOOTH -> iconElement?.setImageResource(R.drawable.ic_bluetooth)
            ReminderType.LOCATION -> iconElement?.setImageResource(R.drawable.ic_location)
            ReminderType.TIME -> iconElement?.setImageResource(R.drawable.ic_time)
        }


        convertView?.findViewById<RelativeLayout>(R.id.rlDelete)?.setOnClickListener {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
        }

        val swipe = convertView?.findViewById<SwipeLayout>(R.id.slReminders)

        swipe?.setOnClickListener{
            if(!isOpen(position))
                swipe.open()
            else
                swipe.close()
        }
    }

    override fun getItem(p0: Int): Any {
        return reminders[p0]
    }

    override fun getItemId(p0: Int): Long {
        return reminders[p0].id
    }

    override fun getCount(): Int {
        return reminders.size
    }

}