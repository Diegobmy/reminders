package com.ragabuza.personalreminder.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.places.ui.PlacePicker
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.*
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.model.Favorite
import com.ragabuza.personalreminder.model.Reminder
import kotlinx.android.synthetic.main.activity_configuration.*
import java.util.*

class SettingsActivity : ActivityBase(), OpDialogInterface, IconDialogAdapter.IconResult {
    override fun onIconClick(iconTag: Int, icon: Int, iTag: String?) {
        editableFavorite?.tag = iconTag
        editableFavorite?.icon = icon
        when (iTag) {
            "edit" -> {
                replaceFav(editableFavorite)
            }
            "add" -> {
                addFav(editableFavorite)
            }
        }
    }

    override fun other(text: CharSequence, tag: String?) {
        when (text) {
            trans.reminderType(Reminder.WIFI) ->
                DialogAdapter(this, this, DialogAdapter.WIFI, tag).show()
            trans.reminderType(Reminder.BLUETOOTH) ->
                DialogAdapter(this, this, DialogAdapter.BLUETOOTH, tag).show()
            trans.reminderType(Reminder.LOCATION) -> {
                val requestCode = if (tag == "edit") 1 else 2
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(this), requestCode)
            }
        }
    }

    override fun wifiCall(text: CharSequence, tag: String?) {
        editableFavorite?.type = Reminder.WIFI
        editableFavorite?.condition = text.toString()
        IconDialogAdapter(this, this, tag, favorites).show()
    }

    override fun blueCall(text: CharSequence, tag: String?) {
        editableFavorite?.type = Reminder.BLUETOOTH
        editableFavorite?.condition = text.toString()
        IconDialogAdapter(this, this, tag, favorites).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1, 2 -> {
                    val place = PlacePicker.getPlace(data, this)
                    editableFavorite?.type = Reminder.LOCATION
                    editableFavorite?.condition = "${place.latLng.latitude},${place.latLng.longitude}"
                    editableFavorite?.location = place.address.toString()
                    val tag = if (requestCode == 1) "edit" else "add"
                    IconDialogAdapter(this, this, tag, favorites).show()
                }
            }
        }
    }

    override fun timeCall(date: Calendar, tag: String?) {}
    override fun contactCall(text: CharSequence, tag: String?) {}
    override fun closed(tag: String?) {}
    override fun finishedLoading() {}

    var editableFavorite: Favorite? = null

    private var favorites = mutableListOf<Favorite>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        supportActionBar?.title = getString(R.string.configs)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        favorites = shared.getFavorites()
        refreshList()

        spColorPick.adapter = ColorSpinnerAdapter(this, R.layout.color_spinner_item, listOf(
                ThemeColor(0, R.style.AppTheme, R.color.PurplecolorPrimaryDarker, R.color.PurplecolorPrimaryDark, R.color.PurplecolorPrimary, R.color.PurplecolorPrimaryLight),
                ThemeColor(1, R.style.AppTheme, R.color.PinkcolorPrimaryDarker, R.color.PinkcolorPrimaryDark, R.color.PinkcolorPrimary, R.color.PinkcolorPrimaryLight),
                ThemeColor(2,  R.style.AppThemeGreen, R.color.GreencolorPrimaryDarker, R.color.GreencolorPrimaryDark, R.color.GreencolorPrimary, R.color.GreencolorPrimaryLight)
        ))

        spColorPick.setSelection(shared.getTheme().id)

        if (shared.hasDeleted())
            llLastDeleted.setOnClickListener {
                val dao = ReminderDAO(this)
                dao.add(shared.getLastDeleted())
                dao.close()
                finish()
            }
        else
            llLastDeleted.visibility = View.GONE

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
        shared.setTheme(spColorPick.selectedItem as ThemeColor)
        shared.setFavorites(favorites)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }


    private fun replaceFav(favorite: Favorite?) {
        favorites.forEach {
            if (it.id == favorite?.id) {
                it.type = favorite.type
                it.condition = favorite.condition
            }
        }
        refreshList()
    }

    private fun addFav(favorite: Favorite?) {
        if (favorite != null) {
            favorites.add(favorite)
        }
        refreshList()
    }

    private fun refreshList() {

        val adapter = FavoriteAdapter(this, favorites)

        adapter.setOnFavoriteClickListener(object : FavoriteAdapter.favoriteClickListener {
            override fun edit(favorite: Favorite) {
                editableFavorite = favorite
                DialogAdapter(this@SettingsActivity, this@SettingsActivity, DialogAdapter.TYPE, "edit").show()
            }

            override fun delete(favorite: Favorite) {
                favorites.remove(favorite)
                refreshList()
            }

        })

        llAdd.visibility = if (favorites.size >= 5) View.GONE else View.VISIBLE

        llAdd.setOnClickListener {
            if (favorites.size < 5) {
                editableFavorite = Favorite(favorites.size.toLong(), 1, 1, "", "")
                DialogAdapter(this@SettingsActivity, this@SettingsActivity, DialogAdapter.TYPE, "add").show()
            }
        }

        lvFavorites.adapter = adapter

        lvFavorites.reajustListView()
    }
}
