package com.ragabuza.personalreminder.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ragabuza.personalreminder.R
import kotlinx.android.synthetic.main.popup_share.*
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import com.ragabuza.personalreminder.model.Reminder
import java.util.*


/**
 * Created by diego.moyses on 1/2/2018.
 */
class SharePopupActivity : AppCompatActivity(), OpDialogInterface {
    override fun closed(tag: String?) {
        finish()
    }

    override fun wifiCall(text: CharSequence, tag: String?) {
        val intent = Intent(this, NewReminder::class.java)
        intent.putExtra("type", Reminder.WIFI)
        intent.putExtra("condition", text)
        intent.putExtra("shareText", reminder)
        intent.putExtra("shareExtra", extra)
        startActivity(intent)
    }

    override fun blueCall(text: CharSequence, tag: String?) {
        val intent = Intent(this, NewReminder::class.java)
        intent.putExtra("type", Reminder.BLUETOOTH)
        intent.putExtra("condition", text)
        intent.putExtra("shareText", reminder)
        intent.putExtra("shareExtra", extra)
        startActivity(intent)
    }

    override fun timeCall(date: Calendar, tag: String?) {
        val intent = Intent(this, NewReminder::class.java)
        intent.putExtra("type", Reminder.TIME)
        intent.putExtra("condition", date.time.toString())
        intent.putExtra("shareText", reminder)
        intent.putExtra("shareExtra", extra)
        startActivity(intent)
    }
    private fun simpleCall() {
        val intent = Intent(this, NewReminder::class.java)
        intent.putExtra("type", Reminder.SIMPLE)
        intent.putExtra("shareText", reminder)
        intent.putExtra("shareExtra", extra)
        startActivity(intent)
    }

    override fun other(text: CharSequence, tag: String?) {}
    override fun contactCall(text: CharSequence, tag: String?) {}

    var reminder: String = ""
    var extra: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_share)


        val imageSpan = ImageSpan(this, R.drawable.ic_link_white)

        reminder = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        extra = intent.getStringExtra(Intent.EXTRA_TEXT)

        var urlString: SpannableString
        val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

        if (intent.getStringExtra(Intent.EXTRA_TEXT).toString().matches(regex)) {
            urlString = SpannableString("${intent.getStringExtra(Intent.EXTRA_SUBJECT)}\n\nL ${intent.getStringExtra(Intent.EXTRA_TEXT)}")
            urlString.setSpan(imageSpan, intent.getStringExtra(Intent.EXTRA_SUBJECT).length+2,intent.getStringExtra(Intent.EXTRA_SUBJECT).length+3,0)
            urlString.setSpan(StyleSpan(Typeface.ITALIC), intent.getStringExtra(Intent.EXTRA_SUBJECT).length+2, urlString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        else
            urlString = SpannableString(intent.getStringExtra(Intent.EXTRA_TEXT))

        tvLabel.text = urlString
        ivOutClick.setOnClickListener {
            finish()
        }



        ibBlueShare.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
        }
        ibWifiShare.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.WIFI).show()
        }
        ibLocShare.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.BLUETOOTH).show()
        }
        ibTimeShare.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.TIME).show()
        }
        ibSimpleShare.setOnClickListener {
            simpleCall()
        }


//        safe.setOnClickListener {  }

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}