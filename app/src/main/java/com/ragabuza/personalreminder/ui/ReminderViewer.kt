package com.ragabuza.personalreminder.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.REMINDER
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.SET_DONE
import com.ragabuza.personalreminder.util.TimeString
import com.ragabuza.personalreminder.util.finishAndRemoveTaskCompat
import kotlinx.android.synthetic.main.activity_reminder_viewer.*
import java.util.*


class ReminderViewer : ActivityBase() {

    private fun setDone(reminder: Reminder) {
        val dao = ReminderDAO(this)
        reminder.active = false
        reminder.done = TimeString(Calendar.getInstance()).getSimple()
        dao.alt(reminder)
        dao.close()
    }

    override fun onPause() {
        super.onPause()
        if (willDone)
            finishAndRemoveTaskCompat()
        else
            finish()
    }

    private var willDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_viewer)

        willDone = intent.extras.getBoolean(SET_DONE, false)

        val reminder = intent.extras.get(REMINDER) as Reminder

        tvReminder.movementMethod = ScrollingMovementMethod();


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
                    sendIntent.type = "text/plain"
                    finishAndRemoveTaskCompat()
                    startActivity(sendIntent)
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
                setDone(reminder)
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

        safe.setOnClickListener {        }

    }
}
