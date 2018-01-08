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


/**
 * Created by diego.moyses on 1/2/2018.
 */
class SharePopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_share)


        val imageSpan = ImageSpan(this, R.drawable.ic_link_white)

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

        safe.setOnClickListener {  }

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}