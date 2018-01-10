package com.ragabuza.personalreminder.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipboardManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ragabuza.personalreminder.adapter.ReminderAdapter
import com.ragabuza.personalreminder.model.*
import kotlinx.android.synthetic.main.activity_reminder_list.*
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.adapter.DialogAdapter
import java.util.*
import android.view.View
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.net.ConnectivityManager
import android.support.v4.view.GravityCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.support.v4.view.MenuItemCompat
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.action_item_filter.*
import kotlinx.android.synthetic.main.drawer_header.*


class ReminderList : AppCompatActivity() {

    private lateinit var adapter: ReminderAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val menuItem = menu?.findItem(R.id.filter)
        val actionView = MenuItemCompat.getActionView(menuItem)
        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }
        tvNumberOfRemindersNew.text = "${reminders.count { it.active }} ${getString(R.string.number_reminders_new)}."
        tvNumberOfRemindersOld.text = "${reminders.count { !it.active }} ${getString(R.string.number_reminders_old)}."

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            fabMenu.close(true)
            adapter.closeAllItems()
            drawer_layout.openDrawer(GravityCompat.START)
            return true
        }
        if (item?.itemId == R.id.filter) {
            drawer_layout.closeDrawer(GravityCompat.START)
            btClipboard.visibility = View.GONE
            if (llFilters.visibility == View.VISIBLE) {
                fabMenu.close(true)
                ivFilterIcon.setImageResource(R.drawable.ic_filter_list)
                llFilters.visibility = View.GONE
                if ((adapter.hasFilters() || !etFilterString.text.isNullOrBlank()) && tvFilterActive != null) {
                    tvFilterActive.visibility = View.VISIBLE
                }
            } else {
                fabMenu.close(true)
                ivFilterIcon.setImageResource(R.drawable.ic_filter_list_selected)
                llFilters.visibility = View.VISIBLE
                if (tvFilterActive != null) {
                    tvFilterActive.visibility = View.GONE
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var reminders: List<Reminder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)

        fabMenu.setClosedOnTouchOutside(true)

        setupDrawer()

        this.supportActionBar!!.title = getString(R.string.remindersActivityTitle)

        reminders = listOf(
                Reminder(1, true, "fnvuierfniwraeonfgiureagnraiugnreaiungreaiungeaiugnuirangraeiungreiuangs", ReminderType.SIMPLE, ReminderWhen.IS, ReminderWhat.CONTACT, "a"),
                Reminder(2, true, "fnvuierfniwraeonfgiureagnraiugnreaiungreaiungeaiugnuirangraeiungreiuang1", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "b"),
                Reminder(3, true, "fnvuierfniwraeonfgiureagnraiugnreaiungreaiungeaiugnuirangraeiungreiuangreaiu12", ReminderType.SIMPLE, ReminderWhen.IS, ReminderWhat.CONTACT, "c"),
                Reminder(4, true, "fnvuierfniwraeonfgiureagnraiugnreaiungreaiungeaiugnuirangra", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "d", "https://www.4site.com.br"),
                Reminder(5, true, "", ReminderType.BLUETOOTH, ReminderWhen.IS, ReminderWhat.CONTACT, "e", "https://www.5site.com.br"),
                Reminder(6, true, "6", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "f", "https://www.6site.com.br"),
                Reminder(7, false, "7", ReminderType.LOCATION, ReminderWhen.IS, ReminderWhat.CONTACT, "g", "https://www.7site.com.br"),
                Reminder(8, false, "8", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "h"),
                Reminder(9, false, "9", ReminderType.BLUETOOTH, ReminderWhen.IS, ReminderWhat.CONTACT, "i"),
                Reminder(10, false, "10", ReminderType.WIFI, ReminderWhen.IS, ReminderWhat.CONTACT, "j"),
                Reminder(11, false, "11", ReminderType.LOCATION, ReminderWhen.IS, ReminderWhat.CONTACT, "k", "https://www.11site.com.br"),
                Reminder(12, false, "12", ReminderType.TIME, ReminderWhen.IS, ReminderWhat.CONTACT, "l")
        )

        val mutAlarms = reminders.toMutableList()
        adapter = ReminderAdapter(this, mutAlarms)
        adapter.doFilter(adapter.newRemindersFilter, true)
        lvRemind.adapter = adapter
        lvRemind.emptyView = tvEmpty

        fabMenu.setOnClickListener { adapter.closeAllItems() }
        lvRemind.setOnItemClickListener { _, _, _, _ ->
            adapter.closeAllItems()
        }

        fabBluetooth.setOnClickListener {
            DialogAdapter(this, "B").show()
        }
        fabWifi.setOnClickListener {
            DialogAdapter(this, "W").show()
        }
        fabTime.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)

            val day = mcurrentTime.get(Calendar.DAY_OF_MONTH)
            val month = mcurrentTime.get(Calendar.MONTH)
            val year = mcurrentTime.get(Calendar.YEAR)


            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, _, _, _ -> }, year, month, day)

            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, _, _ -> datePicker.show() }, hour, minute, true)

            timePicker.show()

        }
        fabLocation.setOnClickListener {
            //            val PLACE_PICKER_REQUEST = 1
//            val builder = PlacePicker.IntentBuilder()
//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }

        setupFilter()

    }


    private fun setupFilter() {
        var ctrlBluetooth = false
        var ctrlWifi = false
        var ctrlTime = false
        var ctrlLocation = false
        var actual: Boolean

        fun resetAll() {
            ctrlBluetooth = false
            ctrlWifi = false
            ctrlTime = false
            ctrlLocation = false
            ibFilterBluetooth.setImageResource(R.drawable.ic_bluetooth_white)
            ibFilterWifi.setImageResource(R.drawable.ic_wifi_white)
            ibFilterLocation.setImageResource(R.drawable.ic_location_white)
            ibFilterTime.setImageResource(R.drawable.ic_time_white)
        }

        ibFilterBluetooth.setOnClickListener {
            actual = !ctrlBluetooth
            resetAll()
            ctrlBluetooth = actual
            ibFilterBluetooth.setImageResource(if (ctrlBluetooth) R.drawable.ic_bluetooth_selected else R.drawable.ic_bluetooth_white)
            adapter.doFilter(adapter.bluetoothFilter, ctrlBluetooth, etFilterString.text.toString())
        }

        ibFilterWifi.setOnClickListener {
            actual = !ctrlWifi
            resetAll()
            ctrlWifi = actual
            ibFilterWifi.setImageResource(if (ctrlWifi) R.drawable.ic_wifi_selected else R.drawable.ic_wifi_white)
            adapter.doFilter(adapter.wifiFilter, ctrlWifi, etFilterString.text.toString())
        }

        ibFilterLocation.setOnClickListener {
            actual = !ctrlLocation
            resetAll()
            ctrlLocation = actual
            ibFilterLocation.setImageResource(if (ctrlLocation) R.drawable.ic_location_selected else R.drawable.ic_location_white)
            adapter.doFilter(adapter.locationFilter, ctrlLocation, etFilterString.text.toString())
        }

        ibFilterTime.setOnClickListener {
            actual = !ctrlTime
            resetAll()
            ctrlTime = actual
            ibFilterTime.setImageResource(if (ctrlTime) R.drawable.ic_time_selected else R.drawable.ic_time_white)
            adapter.doFilter(adapter.timeFilter, ctrlTime, etFilterString.text.toString())
        }

        etFilterString.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                adapter.doFilter(string = p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun setupDrawer() {
        this.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val inAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.in_change_listview)
        val outAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.out_change_listview)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.RNew -> {
                    lvRemind.startAnimation(inAnimation)
                    adapter.doFilter(adapter.newRemindersFilter)
                    supportActionBar!!.title = getString(R.string.remindersActivityTitle)
                    fabMenu.startAnimation(inAnimation)
                    fabMenu.visibility = View.VISIBLE
                }
                R.id.ROld -> {
                    lvRemind.startAnimation(inAnimation)
                    adapter.doFilter(adapter.oldRemindersFilter)
                    supportActionBar!!.title = getString(R.string.oldReminders)
                    fabMenu.startAnimation(outAnimation)
                    fabMenu.visibility = View.GONE
                }
                R.id.config -> Toast.makeText(this, "config", Toast.LENGTH_SHORT).show()
            }
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        clipShow()
    }

    private fun clipShow() {

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        var clip = clipboard.primaryClip.getItemAt(0).text

        if (!clipboard.primaryClip.getItemAt(0).text.isEmpty() && clipboard.primaryClip.getItemAt(0).text.toString() != sharedPref.getString("clip", "")) {
            editor.putString("clip", clip.toString())
            editor.apply()
            btClipboard.visibility = View.VISIBLE
            clip = clipboard.primaryClip.getItemAt(0).text
            if (clip.length > 150) {
                val spannable = SpannableString("${getString(R.string.clipboard)}\n\"${clip.subSequence(0, 150)}...\"")
                spannable.setSpan(StyleSpan(Typeface.ITALIC), getString(R.string.clipboard).length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                btClipboard.text = spannable
            } else {
                val spannable = SpannableString("${getString(R.string.clipboard)}\n\"$clip\"")
                spannable.setSpan(StyleSpan(Typeface.ITALIC), getString(R.string.clipboard).length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                btClipboard.text = spannable
            }

            val regex = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")

            if (clip.toString().matches(regex))
                btClipboard.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_link, 0, 0, 0)
            else
                btClipboard.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            btClipboard.visibility = View.GONE
        }
    }

}
