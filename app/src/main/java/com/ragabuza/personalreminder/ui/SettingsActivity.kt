package com.ragabuza.personalreminder.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import kotlinx.android.synthetic.main.activity_configuration.*
import java.util.*

class SettingsActivity : AppCompatActivity(), OpDialogInterface {
    override fun wifiCall(text: CharSequence, tag: String?) {
        when (tag){
            "home" -> etHome.setText(text)
            "work" -> etWork.setText(text)
        }
    }

    override fun blueCall(text: CharSequence, tag: String?) {    }
    override fun timeCall(date: Calendar, tag: String?) {    }
    override fun other(text: CharSequence, tag: String?) {    }
    override fun contactCall(text: CharSequence, tag: String?) {    }
    override fun closed(tag: String?) {    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        supportActionBar?.title = getString(R.string.remindersActivityTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etHome.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, this, DialogAdapter.WIFI, "home").show()
        }

        etHome.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.WIFI, "home").show()
        }
        etWork.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, this, DialogAdapter.WIFI, "work").show()
        }

        etWork.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.WIFI, "work").show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }
}
