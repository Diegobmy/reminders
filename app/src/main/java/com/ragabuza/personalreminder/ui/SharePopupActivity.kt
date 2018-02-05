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
import com.google.android.gms.location.places.ui.PlacePicker
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.Shared
import java.util.*


/**
 * Created by diego.moyses on 1/2/2018.
 */
class SharePopupActivity : AppCompatActivity(), OpDialogInterface {
    override fun finishedLoading() {}

    override fun closed(tag: String?) {
        finish()
    }

    override fun wifiCall(text: CharSequence, tag: String?) {
        shareIntent.putExtra("type", Reminder.WIFI)
        shareIntent.putExtra("condition", text)
        shareIntent.putExtra("shareText", reminder)
        shareIntent.putExtra("shareExtra", extra)
        startActivity(shareIntent)
    }

    override fun blueCall(text: CharSequence, tag: String?) {
        shareIntent.putExtra("type", Reminder.BLUETOOTH)
        shareIntent.putExtra("condition", text)
        shareIntent.putExtra("shareText", reminder)
        shareIntent.putExtra("shareExtra", extra)
        startActivity(shareIntent)
    }

    override fun timeCall(date: Calendar, tag: String?) {
        shareIntent.putExtra("type", Reminder.TIME)
        shareIntent.putExtra("condition", date.time.toString())
        shareIntent.putExtra("shareText", reminder)
        shareIntent.putExtra("shareExtra", extra)
        startActivity(intent)
    }

    private fun simpleCall() {
        shareIntent.putExtra("type", Reminder.SIMPLE)
        shareIntent.putExtra("shareText", reminder)
        shareIntent.putExtra("shareExtra", extra)
        startActivity(shareIntent)

    }

    private fun locationCall(location: String) {
        shareIntent.putExtra("type", Reminder.LOCATION)
        shareIntent.putExtra("condition", location)
        shareIntent.putExtra("shareText", reminder)
        shareIntent.putExtra("shareExtra", extra)
        startActivity(shareIntent)
    }

    override fun other(text: CharSequence, tag: String?) {}
    override fun contactCall(text: CharSequence, tag: String?) {}

    var reminder: String = ""
    var extra: String = ""
    lateinit var pref: Shared
    lateinit var shareIntent: Intent

    private var goingToMap: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_share)

        pref = Shared(this)
        shareIntent = Intent(this, NewReminder::class.java)
        shareIntent.putExtra("killIt", true)

        val imageSpan = ImageSpan(this, R.drawable.ic_link_white)

        reminder = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        extra = intent.getStringExtra(Intent.EXTRA_TEXT)

        var urlString: SpannableString
        val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

        if (intent.getStringExtra(Intent.EXTRA_TEXT).toString().matches(regex)) {
            urlString = SpannableString("${intent.getStringExtra(Intent.EXTRA_SUBJECT)}\n\nL ${intent.getStringExtra(Intent.EXTRA_TEXT)}")
            urlString.setSpan(imageSpan, intent.getStringExtra(Intent.EXTRA_SUBJECT).length + 2, intent.getStringExtra(Intent.EXTRA_SUBJECT).length + 3, 0)
            urlString.setSpan(StyleSpan(Typeface.ITALIC), intent.getStringExtra(Intent.EXTRA_SUBJECT).length + 2, urlString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else
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
            goingToMap = true
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }
        ibTimeShare.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.TIME).show()
        }
        ibSimpleShare.setOnClickListener {
            simpleCall()
        }
        ibInHouseShare.setOnClickListener {
            wifiCall(pref.getHome(), "")
        }
        ibOutHouseShare.setOnClickListener {
            shareIntent.putExtra("isOut", true)
            wifiCall(pref.getHome(), "")
        }
        ibInWorkShare.setOnClickListener {
            wifiCall(pref.getWork(), "")
        }
        ibOutWorkShare.setOnClickListener {
            shareIntent.putExtra("isOut", true)
            wifiCall(pref.getWork(), "")
        }


//        safe.setOnClickListener {  }

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        if (!goingToMap)
            finish()
    }

    val PLACE_PICKER_REQUEST = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        goingToMap = false
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(data, this)
                locationCall("${place.latLng.latitude},${place.latLng.longitude}")
            }
        }
    }

}