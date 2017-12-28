package com.ragabuza.personalreminder.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.ragabuza.personalreminder.Model.Reminder
import com.ragabuza.personalreminder.R
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl



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

        convertView?.findViewById<TextView>(R.id.tvName)?.text = reminder.reminder.toString()

        convertView?.findViewById<RelativeLayout>(R.id.rlDelete)?.setOnClickListener{
            Toast.makeText(context, "deletou eita", Toast.LENGTH_LONG).show()
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