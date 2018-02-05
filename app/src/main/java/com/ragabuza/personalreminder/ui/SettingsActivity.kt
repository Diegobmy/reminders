package com.ragabuza.personalreminder.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import com.ragabuza.personalreminder.adapter.OpDialogInterface
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.util.Shared
import kotlinx.android.synthetic.main.activity_configuration.*
import java.util.*

class SettingsActivity : AppCompatActivity(), OpDialogInterface {
    override fun finishedLoading() {}

    lateinit var pref: Shared

    override fun wifiCall(text: CharSequence, tag: String?) {
        when (tag) {
            "home" -> etHome.setText(text)
            "work" -> etWork.setText(text)
        }
    }

    override fun blueCall(text: CharSequence, tag: String?) {}
    override fun timeCall(date: Calendar, tag: String?) {}
    override fun other(text: CharSequence, tag: String?) {}
    override fun contactCall(text: CharSequence, tag: String?) {}
    override fun closed(tag: String?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        supportActionBar?.title = getString(R.string.configs)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        FocusGetter.requestFocus()
        pref = Shared(this)

        etHome.setText(pref.getHome())
        etHome.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, this, DialogAdapter.WIFI, "home").show()
        }

        etHome.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.WIFI, "home").show()
        }

        etWork.setText(pref.getWork())
        etWork.setOnFocusChangeListener { _, b ->
            if (b) DialogAdapter(this, this, DialogAdapter.WIFI, "work").show()
        }

        etWork.setOnClickListener {
            DialogAdapter(this, this, DialogAdapter.WIFI, "work").show()
        }

        if (pref.hasDeleted())
            btSettingsUndo.setOnClickListener {
                val dao = ReminderDAO(this)
                dao.add(pref.getLastDeleted())
                dao.close()
                finish()
            }
        else
            btSettingsUndo.visibility = View.GONE

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        if (item?.itemId == R.id.apply) {
            applyConfig()
        }
        return true
    }

    private fun applyConfig() {
        pref.setHome(etHome.text.toString())
        pref.setWork(etWork.text.toString())
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }
}
