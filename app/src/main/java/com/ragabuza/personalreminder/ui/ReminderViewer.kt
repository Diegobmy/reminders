package com.ragabuza.personalreminder.ui

import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.OptionsSpinnerAdapter
import com.ragabuza.personalreminder.adapter.SpinnerItem
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.REMINDER
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.SET_DONE
import com.ragabuza.personalreminder.util.TimeString
import com.ragabuza.personalreminder.util.finishAndRemoveTaskCompat
import kotlinx.android.synthetic.main.activity_reminder_viewer.*
import java.util.*
import com.ragabuza.personalreminder.R.string.clipboard
import android.R.attr.label
import android.content.ClipData
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.PRIVATE
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.PRIVATE_THEME_TRANSPARENT


class ReminderViewer : ActivityBase() {

    private fun setDone() {
        val dao = ReminderDAO(this)
        reminder.active = false
        reminder.done = TimeString(Calendar.getInstance()).getSimple()
        dao.alt(reminder)
        dao.close()
    }

    override fun applyTheme() {
        if (intent.extras.getBoolean(PRIVATE, false))
            theme.applyStyle(PRIVATE_THEME_TRANSPARENT, true)
        else
            theme.applyStyle(shared.getTheme().themeTransparent, true)
    }

    override fun onPause() {
        super.onPause()
        if (willDone) {
            setDone()
            finishAndRemoveTaskCompat()
        }
        else
            finish()
    }

    private var willDone: Boolean = false

    private lateinit var reminder: Reminder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_viewer)

        willDone = intent.extras.getBoolean(SET_DONE, false)

        reminder = intent.extras.get(REMINDER) as Reminder

        tvReminder.movementMethod = ScrollingMovementMethod()

        val options = mutableListOf(
                SpinnerItem(getString(R.string.copy_note), R.drawable.ic_content_copy)
        )

        if (reminder.extra.isNotEmpty())
            options.add(SpinnerItem(getString(R.string.copy_extra), R.drawable.ic_content_copy))

        if (reminder.type != Reminder.SIMPLE && !intent.extras.getBoolean(PRIVATE, false))
            options.add(SpinnerItem(getString(R.string.reshedule), R.drawable.ic_schedule))

        val adapter = OptionsSpinnerAdapter(this, R.layout.spinner_item, options, R.layout.spinner_base)

        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        adapter.setItemClick(spViewOptions, object : OptionsSpinnerAdapter.SpinnerItemClick {
            override fun onSpinnerClick(item: Int) {
                when (options[item].text) {
                    getString(R.string.copy_note) -> {
                        val clip = ClipData.newPlainText("reminder", reminder.reminder)
                        clipboard.primaryClip = clip
                    }
                    getString(R.string.copy_extra) -> {
                        val clip = ClipData.newPlainText("extra", reminder.extra)
                        clipboard.primaryClip = clip
                    }
                    getString(R.string.reshedule) -> {
                        val inte = Intent(this@ReminderViewer, NewReminder::class.java)
                        inte.putExtra(REMINDER, reminder)
                        startActivity(inte)
                        finish()
                    }
                }
            }
        })

        spViewOptions.adapter = adapter

        tvType.text = trans.reminderType(reminder.type)
        tvType.setCompoundDrawablesWithIntrinsicBounds(trans.reminderIcon(reminder.type), 0, 0, 0)
        tvRWhen.text = trans.getViewer(reminder)

        when {
            trans.extraIsLink(reminder.extra) -> {
                llLink.visibility = View.VISIBLE
                tvLink.text = reminder.extra
                btOpen.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(reminder.extra))
                    finishAndRemoveTaskCompat()
                    startActivity(browserIntent)
                }
                btShare.setOnClickListener {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, reminder.extra)
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, reminder.reminder)
                    sendIntent.type = "text/plain"
                    finishAndRemoveTaskCompat()
                    startActivity(Intent.createChooser(sendIntent, "Share"))
                }
            }
            trans.extraIsContact(reminder.extra) -> {
                llPhone.visibility = View.VISIBLE
                tvPhone.text = trans.parseContact(reminder.extra)
                btCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${trans.parsePhone(reminder.extra)}")
                    finishAndRemoveTaskCompat()
                    startActivity(intent)
                }
            }
            reminder.extra.isEmpty() -> {
            }
            else -> {
                tvExtraSimple.visibility = View.VISIBLE
                tvExtraSimple.text = reminder.extra
            }
        }

        if (willDone) {
            btDone.visibility = View.VISIBLE
            btDone.setOnClickListener {
                finishAndRemoveTaskCompat()
            }
        }

        ivOutClick.setOnClickListener {
            finishAndRemoveTaskCompat()
        }

        ibClose.setOnClickListener {
            finishAndRemoveTaskCompat()
        }


        if (reminder.reminder.isNotEmpty()) {
            tvReminder.text = reminder.reminder
        } else {
            tvReminder.text = reminder.extra
            tvExtraSimple.visibility = View.GONE
            tvPhone.visibility = View.GONE
            tvLink.visibility = View.GONE
        }

        safe.setOnClickListener { }

    }
}
