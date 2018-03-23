package com.ragabuza.personalreminder.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.view.View
import com.google.android.gms.location.places.ui.PlacePicker
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import com.ragabuza.personalreminder.model.Favorite
import com.ragabuza.personalreminder.model.Reminder
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.IS_OUT
import com.ragabuza.personalreminder.util.Constants.Intents.Companion.KILL_IT
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_CONDITION
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_EXTRA
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_REMINDER
import com.ragabuza.personalreminder.util.Constants.ReminderFields.Companion.FIELD_TYPE
import com.ragabuza.personalreminder.util.Shared
import kotlinx.android.synthetic.main.popup_share.*
import java.util.*


/**
 * Created by diego.moyses on 1/2/2018.
 */
class SharePopupActivity : ActivityBase(), OpDialogInterface {
    override fun finishedLoading() {}

    override fun closed(tag: String?) {
        finish()
    }

    override fun wifiCall(text: CharSequence, tag: String?) {
        shareIntent.putExtra(FIELD_TYPE, Reminder.WIFI)
        shareIntent.putExtra(FIELD_CONDITION, text)
        shareIntent.putExtra(FIELD_REMINDER, reminder)
        shareIntent.putExtra(FIELD_EXTRA, extra)
        startActivity(shareIntent)
    }

    override fun blueCall(text: CharSequence, tag: String?) {
        shareIntent.putExtra(FIELD_TYPE, Reminder.BLUETOOTH)
        shareIntent.putExtra(FIELD_CONDITION, text)
        shareIntent.putExtra(FIELD_REMINDER, reminder)
        shareIntent.putExtra(FIELD_EXTRA, extra)
        startActivity(shareIntent)
    }

    override fun timeCall(date: Calendar, tag: String?) {
        shareIntent.putExtra(FIELD_TYPE, Reminder.TIME)
        shareIntent.putExtra(FIELD_CONDITION, date.time.toString())
        shareIntent.putExtra(FIELD_REMINDER, reminder)
        shareIntent.putExtra(FIELD_EXTRA, extra)
        startActivity(shareIntent)
    }

    private fun simpleCall() {
        shareIntent.putExtra(FIELD_TYPE, Reminder.SIMPLE)
        shareIntent.putExtra(FIELD_REMINDER, reminder)
        shareIntent.putExtra(FIELD_EXTRA, extra)
        startActivity(shareIntent)

    }

    private fun locationCall(location: String) {
        shareIntent.putExtra(FIELD_TYPE, Reminder.LOCATION)
        shareIntent.putExtra(FIELD_CONDITION, location)
        shareIntent.putExtra(FIELD_REMINDER, reminder)
        shareIntent.putExtra(FIELD_EXTRA, extra)
        startActivity(shareIntent)
    }

    override fun other(text: CharSequence, tag: String?) {}
    override fun contactCall(text: CharSequence, tag: String?) {}

    var reminder: String = ""
    var extra: String = ""
    lateinit var pref: Shared
    lateinit var shareIntent: Intent

    private var goingToMap: Boolean = false

    override fun applyTheme() {
        theme.applyStyle(shared.getTheme().themeTransparent, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_share)

        shareIntent = Intent(this, NewReminder::class.java)
        shareIntent.putExtra(KILL_IT, true)

        val imageSpan = ImageSpan(this, R.drawable.ic_link_white)

        reminder = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        extra = intent.getStringExtra(Intent.EXTRA_TEXT)

        var urlString: SpannableString

        if (trans.extraIsLink(intent.getStringExtra(Intent.EXTRA_TEXT).toString())) {
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

        val favorites = shared.getFavorites()

        if(favorites.isEmpty()){
            llCustoms.visibility = View.GONE
            tvCustoms.visibility = View.GONE
        } else {
            //1
            custom1.visibility = View.VISIBLE
            custom1.setImageResource(favorites[0].icon)
            custom1.setOnClickListener {
                favoriteCall(favorites[0])
            }
            //2
            if (favorites.size > 1){
                custom2.visibility = View.VISIBLE
                custom2.setImageResource(favorites[1].icon)
                custom2.setOnClickListener {
                    favoriteCall(favorites[1])
                }
            }
            //3
            if (favorites.size > 2){
                custom3.visibility = View.VISIBLE
                custom3.setImageResource(favorites[2].icon)
                custom3.setOnClickListener {
                    favoriteCall(favorites[2])
                }
            }
            //4
            if (favorites.size > 3){
                custom4.visibility = View.VISIBLE
                custom4.setImageResource(favorites[3].icon)
                custom4.setOnClickListener {
                    favoriteCall(favorites[3])
                }
            }
            //5
            if (favorites.size > 4){
                custom5.visibility = View.VISIBLE
                custom5.setImageResource(favorites[4].icon)
                custom5.setOnClickListener {
                    favoriteCall(favorites[4])
                }
            }
        }


    }

    private fun favoriteCall(favorite: Favorite) {
        shareIntent.putExtra(FIELD_TYPE, favorite.type)
        shareIntent.putExtra(FIELD_CONDITION, favorite.condition)
        shareIntent.putExtra(FIELD_REMINDER, reminder)
        shareIntent.putExtra(FIELD_EXTRA, extra)
        startActivity(shareIntent)
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