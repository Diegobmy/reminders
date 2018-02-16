package com.ragabuza.personalreminder.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
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

    private lateinit var passwordDialog: PasswordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myPassword = shared.getPassword()
        myBiometric = shared.hasFingerprint()

        setContentView(R.layout.activity_configuration)
        supportActionBar?.title = getString(R.string.configs)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        favorites = shared.getFavorites()
        refreshList()

        val appThemes = listOf(
                ThemeColor(0, "Roxo(Padrão)", R.style.AppTheme, R.style.Theme_Transparent, R.color.PurplePrimaryDarker, R.color.PurplePrimaryDark, R.color.PurplePrimary, R.color.PurplePrimaryLight),
                ThemeColor(1, "Verde", R.style.AppThemeGreen, R.style.AppThemeGreen_Transparent, R.color.GreenPrimaryDarker, R.color.GreenPrimaryDark, R.color.GreenPrimary, R.color.GreenPrimaryLight),
                ThemeColor(2, "Vermelho", R.style.AppThemeRed, R.style.AppThemeRed_Transparent, R.color.RedPrimaryDarker, R.color.RedPrimaryDark, R.color.RedPrimary, R.color.RedPrimaryLight),
                ThemeColor(3, "Azul", R.style.AppThemeBlue, R.style.AppThemeBlue_Transparent, R.color.BluePrimaryDarker, R.color.BluePrimaryDark, R.color.BluePrimary, R.color.BluePrimaryLight),
                ThemeColor(4, "Amarelo", R.style.AppThemeYellow, R.style.AppThemeYellow_Transparent, R.color.YellowPrimaryDarker, R.color.YellowPrimaryDark, R.color.YellowPrimary, R.color.YellowPrimaryLight),
                ThemeColor(5, "Rosa", R.style.AppThemePink, R.style.AppThemePink_Transparent, R.color.PinkPrimaryDarker, R.color.PinkPrimaryDark, R.color.PinkPrimary, R.color.PinkPrimaryLight),
                ThemeColor(6, "Cinza", R.style.AppThemeGray, R.style.AppThemeGray_Transparent, R.color.GrayPrimaryDarker, R.color.GrayPrimaryDark, R.color.GrayPrimary, R.color.GrayPrimaryLight),
                ThemeColor(7, "AMOLED", R.style.AppThemeAmoled, R.style.AppThemeAmoled_Transparent, R.color.AmoledPrimaryDarker, R.color.AmoledPrimaryDark, R.color.AmoledPrimary, R.color.AmoledPrimaryLight),
                ThemeColor(8, "Especial", R.style.AppThemeRainbow, R.style.AppThemeRainbow_Transparent, R.color.RainbowPrimaryDarker, R.color.RainbowPrimaryDark, R.color.RainbowPrimary, R.color.RainbowPrimaryLight)
        )

        spColorPick.adapter = ColorSpinnerAdapter(this, R.layout.color_spinner_item, appThemes)
        spColorPick.setSelection(shared.getTheme().id)

        swPowerSave.isChecked = shared.isPowerSave()
        swNotification.isChecked = shared.isShowNotification()
        if (shared.hasPassword()) {
            swPassword.isChecked = true
            llConfigPassword.visibility = View.VISIBLE
        }

        passwordDialog = PasswordAdapter(this, object : PasswordAdapter.PasswordResult {
            override fun onCancel(ignore: Boolean) {
                if (ignore) return
                swPassword.isChecked = false
                llConfigPassword.visibility = View.GONE
            }

            override fun onSetPassword(password: String, fingerprint: Boolean) {
                myPassword = password
                myBiometric = fingerprint
            }
        })

        swPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                llConfigPassword.visibility = View.VISIBLE
                passwordDialog.show(myPassword, myBiometric)
            } else
                llConfigPassword.visibility = View.GONE
        }

        llConfigPassword.setOnClickListener {
            requestPassword()
        }

        llDeleteAll.setOnClickListener {

            val alert = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(this.getString(R.string.really_delete_all))
                    .setContentText(this.getString(R.string.cannot_be_undone))
                    .setConfirmText(this.getString(R.string.yes_delete))
                    .setCancelText(this.getString(R.string.no_delete))
                    .setConfirmClickListener {
                        it.dismiss()
                        ConfirmAdapter(this, object : ConfirmAdapter.ConfirmResult {
                            override fun onConfirm() {
                                val dao = ReminderDAO(this@SettingsActivity)
                                Toast.makeText(
                                        this@SettingsActivity,
                                        "${dao.removeEverything()}  registros deletados.",
                                        Toast.LENGTH_LONG
                                ).show()
                                shared.setLastDeleted(null)
                                dao.close()
                                finish()
                            }
                        }).show()
                    }
            alert.show()
        }

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

    override fun requestPasswordCallback(success: Boolean) {
        if (success)
            passwordDialog.show(myPassword, myBiometric)
    }

    private lateinit var myPassword: String
    private var myBiometric: Boolean = false

    private fun applyConfig() {
        shared.setTheme(spColorPick.selectedItem as ThemeColor)
        shared.setFavorites(favorites)
        shared.setPowerSave(swPowerSave.isChecked)
        shared.setShowNotification(swNotification.isChecked)
        if (swPassword.isChecked)
            shared.setPassword(myPassword)
        else {
            shared.setPassword("")
            shared.setFingerprint(false)
        }
        shared.setFingerprint(myBiometric)
        val b = Intent(this, ReminderList::class.java)
        b.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(b)
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

        lvFavorites.readjustListView()
    }
}
