package com.ragabuza.personalreminder.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.ReminderTranslation
import kotlinx.android.synthetic.main.activity_reminder_viewer.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.util.TimeString
import com.ragabuza.personalreminder.util.finishAndRemoveTaskCompat
import java.util.*


class ReminderViewer : AppCompatActivity() {

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
            finish()
    }

    private var willDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_viewer)

        willDone = intent.extras.getBoolean("setDone", false)

        val translator = ReminderTranslation(this)

        val reminder = intent.extras.get("Reminder") as Reminder

        if (willDone)
            setDone(reminder)

        tvType.text = translator.reminderType(reminder.type)
        tvType.setCompoundDrawablesWithIntrinsicBounds(translator.reminderIcon(reminder.type), 0, 0, 0)

            tvRWhen.text = translator.getNotification(reminder)

        when {
            translator.extraIsLink(reminder.extra) -> {
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
            translator.extraIsContact(reminder.extra) -> {
                llPhone.visibility = View.VISIBLE
                tvPhone.text = translator.parseContact(reminder.extra)
                btCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${translator.parsePhone(reminder.extra)}")
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

        tvReminder.text = reminder.reminder
        ivOutClick.setOnClickListener {
            finish()
        }
        ibClose.setOnClickListener {
            finish()
        }
    }
}
