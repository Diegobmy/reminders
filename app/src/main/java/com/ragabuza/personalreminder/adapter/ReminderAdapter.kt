package com.ragabuza.personalreminder.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
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
import com.ragabuza.personalreminder.util.Constants.Other.Companion.PRIVATE_FOLDER
import com.ragabuza.personalreminder.util.ReminderTranslation
import com.ragabuza.personalreminder.util.TimeString
import kotlinx.android.synthetic.main.activity_reminder.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by diego.moyses on 12/28/2017.
 */
class ReminderAdapter(private val context: Context, val reminders: MutableList<Reminder>) : BaseSwipeAdapter() {

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.slReminders
    }

    interface ReminderClickCallback {
        fun edit(reminder: Reminder)
        fun delete(reminder: Reminder)
        fun view(reminder: Reminder)
        fun requestRefresh()
        fun getType(): Boolean
    }

    val originalList: List<Reminder> = reminders.toList()
    private val viewList = mutableListOf<View>()

    override fun generateView(position: Int, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)

        return inflater.inflate(R.layout.list_item, parent, false)
    }

    val bluetoothFilter = 1
    val wifiFilter = 2
    val locationFilter = 3
    val timeFilter = 4
    val simpleFilter = 5

    private val filters = mutableListOf<Int>()

    fun hasFilters(): Boolean {
        return (filters.contains(bluetoothFilter) || filters.contains(wifiFilter)
                || filters.contains(timeFilter) || filters.contains(locationFilter) || filters.contains(simpleFilter))
    }

    fun doFilter(type: Int = 0, putting: Boolean = true, str: String = "", clearAll: Boolean = false, folder: String = "*") {

        var string = str.trim()

        if (putting && type != 0)
            filters.add(type)
        else if (!putting)
            filters.remove(type)

        val toRemove = mutableListOf<Reminder>()
        val toAdd = mutableListOf<Reminder>()

        if (clearAll) {
            filters.clear()
            string = ""
        }

        filters.forEach { t ->
            when (t) {
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
        when (folder) {
            "*" -> {

            }
            "" -> {
                originalList.filterTo(toRemove) {
                    it.folder.isNotEmpty()
                }
            }
            else -> {
                originalList.filterTo(toRemove) {
                    it.folder != folder
                }
            }
        }
        reminders.clear()
        if (filters.isNotEmpty()) {
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
        val background = convertView?.findViewById<LinearLayout>(R.id.llUpper)

        val iconElement = convertView?.findViewById<ImageView>(R.id.ivIcon)
        iconElement?.visibility = View.VISIBLE

        if (reminder.type == Reminder.TIME) {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
            cal.time = sdf.parse(reminder.condition)
            tvCond?.text = TimeString(context, cal).getString(true)
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

        if (reminder.done != "WAITING") {
            tvDone?.text = reminder.done
            background?.setBackgroundColor(context.resources.getColor(android.R.color.white))
        } else {
            tvDone?.text = ""
            background?.setBackgroundColor(context.resources.getColor(R.color.importantReminder))
            val ss = SpannableString(context.getString(R.string.reminder_triggered))
            ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.importantReminderText)), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(StyleSpan(Typeface.BOLD), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvCond?.text = ss
            iconElement?.setImageResource(R.drawable.ic_important)
        }


        convertView?.findViewById<RelativeLayout>(R.id.rlDelete)?.setOnClickListener {
            val alert = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(context.getString(R.string.really_delete))
                    .setContentText(context.getString(R.string.cannot_be_undone))
                    .setConfirmText(context.getString(R.string.no_delete))
                    .setCancelText(context.getString(R.string.yes_delete))
                    .setCancelClickListener {
                        (context as ReminderClickCallback).delete(reminder)
                        it.setTitleText(context.getString(R.string.deleted))
                                .setContentText(context.getString(R.string.reminder_deleted))
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                        closeAllItems()
                        notifyDataSetChanged()
                    }
            alert.show()
        }
        convertView?.findViewById<RelativeLayout>(R.id.rlEdit)?.setOnClickListener {
            (context as ReminderClickCallback).edit(reminder)
        }
        convertView?.findViewById<RelativeLayout>(R.id.rlView)?.setOnClickListener {
            (context as ReminderClickCallback).view(reminder)
        }

        val swipe = convertView?.findViewById<SwipeLayout>(R.id.slReminders)

        convertView?.findViewById<TextView>(R.id.tvName)?.setOnClickListener {
            if (!isOpen(position))
                swipe?.open()
            else
                swipe?.close()
        }
        convertView?.findViewById<TextView>(R.id.tvName)?.setOnLongClickListener {
            (context as ReminderClickCallback).view(reminder)
            return@setOnLongClickListener true
        }

        val text: String


        text = reminder.reminder

        val trans = ReminderTranslation(context)

        if (reminder.extra.isNotEmpty()) {
            linkArea?.visibility = View.VISIBLE
            tvLink?.text = reminder.extra
        } else {
            ivBigLink?.visibility = View.GONE
            linkArea?.visibility = View.GONE
        }

        when {
            trans.extraIsContact(reminder.extra) -> {
                ivLink?.setImageResource(R.drawable.ic_contact)
                ivLink?.visibility = View.VISIBLE
                val ss = SpannableString(reminder.extra.substring(9 until reminder.extra.length))
                ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.contactColor)), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvLink?.text = ss
            }
            trans.extraIsLink(reminder.extra) -> {
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
            if (!(context as ReminderClickCallback).getType())
                reminders.filterTo(toRemove) {
                    (!it.active && (reminder.id != it.id))
                }
            else
                reminders.filterTo(toRemove) {
                    (it.active && (reminder.id != it.id))
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
            reminder.done = if (reminder.active) "" else TimeString(context, Calendar.getInstance()).getDone()

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