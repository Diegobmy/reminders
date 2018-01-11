package com.ragabuza.personalreminder.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
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
class ReminderAdapter(private val context: Context, private val reminders: MutableList<Reminder>) : BaseSwipeAdapter() {
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
    val simpleFilter = 7

    private val filters = mutableListOf<Int>()

    fun hasFilters(): Boolean {
        return (filters.contains(bluetoothFilter) || filters.contains(wifiFilter)
                || filters.contains(timeFilter) || filters.contains(locationFilter) || filters.contains(simpleFilter))
    }

    fun doFilter(type: Int = 0, putting: Boolean = true, string: String = "", clearAll: Boolean = false) {

        var string = string.trim()

        if (type in 1..2) {
            filters.remove(1)
            filters.remove(2)
        }

        if (putting && type != 0)
            filters.add(type)
        else if (!putting)
            filters.remove(type)

        val toRemove = mutableListOf<Reminder>()
        val toAdd = mutableListOf<Reminder>()

        if (clearAll) {
            filters.removeAll(listOf(3, 4, 5, 6, 7))
            string = ""
        }

        filters.forEach { t ->
            when (t) {
                newRemindersFilter -> originalList.filterTo(toRemove) { !it.active }
                oldRemindersFilter -> originalList.filterTo(toRemove) { it.active }
                bluetoothFilter -> originalList.filterTo(toAdd) { it.type == ReminderType.BLUETOOTH }
                wifiFilter -> originalList.filterTo(toAdd) { it.type == ReminderType.WIFI }
                locationFilter -> originalList.filterTo(toAdd) { it.type == ReminderType.LOCATION }
                timeFilter -> originalList.filterTo(toAdd) { it.type == ReminderType.TIME }
                simpleFilter -> originalList.filterTo(toAdd) { it.type == ReminderType.SIMPLE }
            }
        }
        originalList.filterTo(toRemove) {
            !(it.reminder.contains(string, true)
                    || it.link.contains(string, true)
                    || it.condition.contains(string, true))
        }
        reminders.clear()
        if (filters.size > 1) {
            reminders.addAll(toAdd)
            reminders.removeAll(toRemove)
        } else {
            reminders.addAll(originalList)
            reminders.removeAll(toRemove)
        }
        notifyDataSetChanged()
    }


    override fun fillValues(position: Int, convertView: View?) {

        val reminder: Reminder = reminders[position]

        val tvLink = convertView?.findViewById<TextView>(R.id.tvLink)
        val tvName = convertView?.findViewById<TextView>(R.id.tvName)
        val ivBigLink = convertView?.findViewById<ImageView>(R.id.ivBigLink)
        val ivCheckbox = convertView?.findViewById<ImageView>(R.id.ivCheckbox)
        val linkArea = convertView?.findViewById<LinearLayout>(R.id.llLinkArea)

        tvName?.text = reminder.reminder

        val iconElement = convertView?.findViewById<ImageView>(R.id.ivIcon)
        iconElement?.visibility = View.VISIBLE
        when (reminder.type) {
            ReminderType.WIFI -> iconElement?.setImageResource(R.drawable.ic_wifi)
            ReminderType.BLUETOOTH -> iconElement?.setImageResource(R.drawable.ic_bluetooth)
            ReminderType.LOCATION -> iconElement?.setImageResource(R.drawable.ic_location)
            ReminderType.TIME -> iconElement?.setImageResource(R.drawable.ic_time)
            ReminderType.SIMPLE -> iconElement?.visibility = View.GONE
        }


        if (reminder.link.isNotEmpty() && reminder.reminder.isNotEmpty()) {
            linkArea?.visibility = View.VISIBLE
            tvLink?.text = reminder.link
        } else if (reminder.reminder.isEmpty()) {
            convertView?.findViewById<ImageView>(R.id.ivBigLink)?.visibility = View.VISIBLE
            linkArea?.visibility = View.GONE
            tvName?.text = reminder.link
        } else {
            ivBigLink?.visibility = View.GONE
            linkArea?.visibility = View.GONE
        }

        convertView?.findViewById<RelativeLayout>(R.id.rlDelete)?.setOnClickListener {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
        }
        convertView?.findViewById<RelativeLayout>(R.id.rlEdit)?.setOnClickListener {
            Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show()
        }

        val swipe = convertView?.findViewById<SwipeLayout>(R.id.slReminders)

        convertView?.findViewById<TextView>(R.id.tvName)?.setOnClickListener {
            if (!isOpen(position))
                swipe?.open()
            else
                swipe?.close()
        }

        val simpleLimit = 77
        val otherLimit = 71

        if (tvName?.text.toString().length > if (reminder.type == ReminderType.SIMPLE) simpleLimit else otherLimit)
            tvName?.text = "${tvName?.text?.subSequence(0, (if (reminder.type == ReminderType.SIMPLE) simpleLimit else otherLimit) - 3)}..."

        val text = tvName?.text
        val span = SpannableString(text)
        span.setSpan(StrikethroughSpan(), 0, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (reminder.active) {
            ivCheckbox?.setImageResource(R.drawable.ic_unchecked_box_grey)
        } else {
            tvName?.text = span
            ivCheckbox?.setImageResource(R.drawable.ic_checked_box_grey)
        }

        convertView?.findViewById<ImageButton>(R.id.ivCheckbox)?.setOnClickListener {
            val toRemove = mutableListOf<Reminder>()
            if (filters.contains(newRemindersFilter)) {
                reminders.filterTo(toRemove) {
                    (!it.active && (reminder.id != it.id))
                }
            } else if (filters.contains(oldRemindersFilter)) {
                reminders.filterTo(toRemove) {
                    (it.active && (reminder.id != it.id))
                }
            }

            reminders.removeAll(toRemove)
            notifyDataSetChanged()

            if (reminder.active) {
                tvName?.text = span
                ivCheckbox?.setImageResource(R.drawable.ic_checked_box_grey)
            } else {
                tvName?.text = text
                ivCheckbox?.setImageResource(R.drawable.ic_unchecked_box_grey)
            }

            reminder.active = !reminder.active
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