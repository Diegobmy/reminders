package com.ragabuza.personalreminder.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.TimeString
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by diego.moyses on 12/28/2017.
 */
class ReminderAdapter(private val context: Context, val reminders: MutableList<Reminder>) : BaseSwipeAdapter() {

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.slReminders
    }

    interface ReminderClickCallback{
        fun edit(reminder: Reminder)
        fun delete(reminder: Reminder)
    }

    val originalList: MutableList<Reminder> = reminders.toMutableList()
    private val viewList = mutableListOf<View>()

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

    fun doFilter(type: Int = 0, putting: Boolean = true, str: String = "", clearAll: Boolean = false) {

        var string = str.trim()

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
                bluetoothFilter -> originalList.filterTo(toAdd) { it.type == Reminder.BLUETOOTH }
                wifiFilter -> originalList.filterTo(toAdd) { it.type == Reminder.WIFI }
                locationFilter -> originalList.filterTo(toAdd) { it.type == Reminder.LOCATION }
                timeFilter -> originalList.filterTo(toAdd) { it.type == Reminder.TIME }
                simpleFilter -> originalList.filterTo(toAdd) { it.type == Reminder.SIMPLE }
            }
        }
        originalList.filterTo(toRemove) {
            !(it.reminder.contains(string, true)
                    || it.extra.contains(string, true)
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

        if (convertView != null) {
            convertView.tag = reminder.id
            viewList.add(convertView)
        }

        val tvLink = convertView?.findViewById<TextView>(R.id.tvLink)
        val tvName = convertView?.findViewById<TextView>(R.id.tvName)
        val tvCond = convertView?.findViewById<TextView>(R.id.tvCondition)
        val tvDone = convertView?.findViewById<TextView>(R.id.tvDone)
        val ivBigLink = convertView?.findViewById<ImageView>(R.id.ivBigLink)
        val ivCheckbox = convertView?.findViewById<ImageView>(R.id.ivCheckbox)
        val linkArea = convertView?.findViewById<LinearLayout>(R.id.llLinkArea)
        val ivLink = convertView?.findViewById<ImageView>(R.id.ivLink)

        val iconElement = convertView?.findViewById<ImageView>(R.id.ivIcon)
        iconElement?.visibility = View.VISIBLE

        if (reminder.type == Reminder.TIME) {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
            cal.time = sdf.parse(reminder.condition)
            tvCond?.text = TimeString(cal).getString(true)
        } else if (reminder.type == Reminder.LOCATION)
            tvCond?.text = reminder.rWhen
        else
            tvCond?.text = reminder.condition

        when (reminder.type) {
            Reminder.WIFI -> iconElement?.setImageResource(R.drawable.ic_wifi)
            Reminder.BLUETOOTH -> iconElement?.setImageResource(R.drawable.ic_bluetooth)
            Reminder.LOCATION -> iconElement?.setImageResource(R.drawable.ic_location)
            Reminder.TIME -> iconElement?.setImageResource(R.drawable.ic_time)
            Reminder.SIMPLE -> iconElement?.visibility = View.GONE
        }

        tvDone?.text = reminder.done

        convertView?.findViewById<RelativeLayout>(R.id.rlDelete)?.setOnClickListener {
            val alert = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(context.getString(R.string.really_delete))
                    .setContentText(context.getString(R.string.cannot_be_undone))
                    .setConfirmText(context.getString(R.string.yes_delete))
                    .setCancelText(context.getString(R.string.no_delete))
                    .setConfirmClickListener {
                        (context as ReminderClickCallback).delete(reminder)
                        originalList.remove(reminder)
                        it.setTitleText(context.getString(R.string.deleted))
                                .setContentText(context.getString(R.string.reminder_deleted))
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        reminders.remove(reminder)
                        closeAllItems()
                        notifyDataSetChanged()
                    }
            alert.show()
        }
        convertView?.findViewById<RelativeLayout>(R.id.rlEdit)?.setOnClickListener {
            (context as ReminderClickCallback).edit(reminder)
        }

        val swipe = convertView?.findViewById<SwipeLayout>(R.id.slReminders)

        convertView?.findViewById<TextView>(R.id.tvName)?.setOnClickListener {
            if (!isOpen(position))
                swipe?.open()
            else
                swipe?.close()
        }

        val text: String


        if (reminder.extra.isNotEmpty() && reminder.reminder.isNotEmpty()) {
            linkArea?.visibility = View.VISIBLE
            tvLink?.text = reminder.extra
            text = reminder.reminder
        } else if (reminder.reminder.isEmpty()) {
            convertView?.findViewById<ImageView>(R.id.ivBigLink)?.visibility = View.VISIBLE
            linkArea?.visibility = View.GONE
            text = reminder.extra
        } else {
            text = reminder.reminder
            ivBigLink?.visibility = View.GONE
            linkArea?.visibility = View.GONE
        }

        val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
        when {
            reminder.extra.contains("CONTACT: ") -> {
                ivLink?.setImageResource(R.drawable.ic_contact)
                ivLink?.visibility = View.VISIBLE
                val ss = SpannableString(reminder.extra.substring(9 until reminder.extra.length))
                ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.contactColor)), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvLink?.text = ss
            }
            reminder.extra.matches(regex) -> {
                ivLink?.visibility = View.VISIBLE
                ivLink?.setImageResource(R.drawable.ic_link)
            }
            else -> ivLink?.visibility = View.GONE
        }

        val span = SpannableString(text)
        span.setSpan(StrikethroughSpan(), 0, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (reminder.active) {
            tvName?.text = text
            ivCheckbox?.setImageResource(R.drawable.ic_unchecked_box_grey)
        } else {
            tvName?.text = span
            ivCheckbox?.setImageResource(R.drawable.ic_checked_box_grey)
        }

        tvName?.ellipsize = TextUtils.TruncateAt.END
        tvLink?.ellipsize = TextUtils.TruncateAt.END
        tvLink?.maxLines = 1

        if (reminder.extra.isEmpty())
            tvName?.maxLines = 2
        else
            tvName?.maxLines = 1

        convertView?.findViewById<ImageButton>(R.id.ivCheckbox)?.setOnClickListener {

            if (reminder.active) {
                tvName?.text = span
                ivCheckbox?.setImageResource(R.drawable.ic_checked_box_grey)
            } else {
                tvName?.text = text
                ivCheckbox?.setImageResource(R.drawable.ic_unchecked_box_grey)
            }

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

            var removeView: View? = null

            if (toRemove.isNotEmpty())
                viewList.forEach {
                    if (toRemove[0].id == it.tag) {
                        fade(it)
                        removeView = it
                    }
                }

            viewList.remove(removeView)

            reminders.removeAll(toRemove)

            shine(convertView)

            convertView.postDelayed(Runnable {
                notifyDataSetChanged()
            }, 300)

            reminder.active = !reminder.active
            reminder.done = if (reminder.active) "" else TimeString(Calendar.getInstance()).getSimple()
            val dao = ReminderDAO(context)
            dao.alt(reminder)
            dao.close()

        }

    }

    private fun fade(element: View?) {
        if (element == null) return
        val anim = ObjectAnimator.ofFloat(element, "alpha", 1f, 0f, 1f)
        anim.duration = 600
        anim.start()
    }

    private fun shine(element: View?) {
        if (element == null) return
        val y = element.translationY
        val anim = ObjectAnimator.ofFloat(element, "translationY", y, y - 20, y)
        anim.duration = 300
        anim.start()
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