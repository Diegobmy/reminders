package com.ragabuza.personalreminder.adapter

import android.content.Context
import android.net.ConnectivityManager
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
class ReminderAdapter(private val context: Context, private val reminders: MutableList<Reminder>, connManager: ConnectivityManager): BaseSwipeAdapter() {
    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.slReminders
    }

    private val originalList: List<Reminder> = reminders.toList()

    override fun generateView(position: Int, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)

        return inflater.inflate(R.layout.list_item, parent, false)
    }

    val newRemindersFilter = 1
    val oldRemindersFilter = 2
    val bluetoothFilter = 3
    val wifiFilter = 4
    val locationFilter = 5
    val timeFilter = 6
    val stringFilter = 7

    private val filters = mutableListOf<Int>()

    fun hasFilters(): Boolean{
        return (filters.contains(bluetoothFilter)||filters.contains(wifiFilter)
                ||filters.contains(timeFilter)||filters.contains(locationFilter)||filters.contains(stringFilter))
    }

    fun doFilter(type: Int = 0, putting: Boolean = true, string: String = ""){
        reminders.clear()
        reminders.addAll(originalList)

        if (putting && type in 1..2) {
            filters.remove(1)
            filters.remove(2)
            filters.add(type)
        } else if (putting && type in 3..6) {
            filters.remove(3)
            filters.remove(4)
            filters.remove(5)
            filters.remove(6)
            filters.add(type)
        } else if (!putting)
            filters.remove(type)

        filters.forEach { t ->
            val toRemove = mutableListOf<Reminder>()
            when (t) {
                newRemindersFilter -> reminders.filterTo(toRemove) { !it.active }
                oldRemindersFilter -> reminders.filterTo(toRemove) { it.active }
                bluetoothFilter -> reminders.filterTo(toRemove) { it.type != ReminderType.BLUETOOTH }
                wifiFilter -> reminders.filterTo(toRemove) { it.type != ReminderType.WIFI }
                locationFilter -> reminders.filterTo(toRemove) { it.type != ReminderType.LOCATION }
                timeFilter -> reminders.filterTo(toRemove) { it.type != ReminderType.TIME }
            }
            reminders.filterTo(toRemove) { !it.reminder.contains(string) }
            reminders.removeAll(toRemove)
        }
        notifyDataSetChanged()
    }


    override fun fillValues(position: Int, convertView: View?) {

        val reminder: Reminder = reminders[position]

        convertView?.findViewById<TextView>(R.id.tvName)?.text = reminder.reminder

        if(reminder.active){
            convertView?.findViewById<LinearLayout>(R.id.llUpper)?.background = context.resources.getDrawable(android.R.color.white)
            convertView?.findViewById<RelativeLayout>(R.id.rlEdit)?.background = context.resources.getDrawable(android.R.color.holo_green_dark)
            convertView?.findViewById<ImageView>(R.id.ivEdit)?.setImageResource(R.drawable.ic_edit)
            convertView?.findViewById<RelativeLayout>(R.id.rlDelete)?.background = context.resources.getDrawable(android.R.color.holo_orange_light)
            convertView?.findViewById<ImageView>(R.id.ivDelete)?.setImageResource(R.drawable.ic_done_white)
        } else {
            convertView?.findViewById<LinearLayout>(R.id.llUpper)?.background = context.resources.getDrawable(android.R.color.darker_gray)
            convertView?.findViewById<RelativeLayout>(R.id.rlEdit)?.background = context.resources.getDrawable(android.R.color.holo_blue_light)
            convertView?.findViewById<ImageView>(R.id.ivEdit)?.setImageResource(R.drawable.restore_white)
            convertView?.findViewById<RelativeLayout>(R.id.rlDelete)?.background = context.resources.getDrawable(android.R.color.holo_red_dark)
            convertView?.findViewById<ImageView>(R.id.ivDelete)?.setImageResource(R.drawable.ic_delete_forever_white)
        }

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
        convertView?.findViewById<RelativeLayout>(R.id.rlEdit)?.setOnClickListener {
            Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show()
        }

        val swipe = convertView?.findViewById<SwipeLayout>(R.id.slReminders)

        convertView?.findViewById<LinearLayout>(R.id.llUpper)?.setOnClickListener{
            if(!isOpen(position))
                swipe?.open()
            else
                swipe?.close()
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