package com.ragabuza.personalreminder.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.wifi.WifiManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.view.View
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*


/**
 * Created by diego.moyses on 1/2/2018.
 */
class DialogAdapter(val context: Context, val activity: Activity, val type: String, val tag: String? = null) {

    private val listener: OpDialogInterface = context as OpDialogInterface

    companion object {
        val WIFI = "W"
        val BLUETOOTH = "B"
        val TIME = "T"
        val CONTACTS = "CON"
        val CHOICE_WEB = "OWEB"
        val CHOICE_LOCATION = "OLOC"
    }

    @SuppressLint("InflateParams")
    fun show() {

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_dialog, null)
        val lv = view.findViewById<ListView>(R.id.lv)
        val webList = mutableListOf<String>()
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val filter = view.findViewById<EditText>(R.id.etFilter)

        view.findViewById<ImageButton>(R.id.ibClose).setOnClickListener {
            listener.closed(tag)
            dialog.dismiss()
        }

        when (type) {
            "W" -> {
                if (wifiDialog(title, webList)) return
            }
            "B" -> {
                if (bluetoothDialog(title, webList)) return
            }
            "T" -> {
                showTimeDialog()
            }
            "CON" -> {
                webList.addAll(getContactList())
            }
            "OWEB" -> {
                webList.add(context.getString(R.string.when_is))
                webList.add(context.getString(R.string.when_isnot))
            }
            "OLOC" -> {
                webList.add(context.getString(R.string.when_is_2))
                webList.add(context.getString(R.string.when_isnot_2))
                filter.visibility = View.GONE
            }
        }


        val linkedHashSet = LinkedHashSet<String>()
        linkedHashSet.addAll(webList)
        adapter.addAll(linkedHashSet)

        lv.adapter = adapter


        filter.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) = Unit

        })

        dialog.setContentView(view)

        view.findViewById<ListView>(R.id.lv).setOnItemClickListener { _, iView, _, _ ->
            val item = iView.findViewById<TextView>(android.R.id.text1)
            if (type == "W")
                listener.wifiCall(item.text, tag)
            else if (type == "B")
                listener.blueCall(item.text, tag)
            else if (type == "OWEB" || type == "OLOC")
                listener.other(item.text, tag)
            else if (type == "CON")
                listener.contactCall(item.text, tag)

            dialog.dismiss()
        }

        if (type != "T")
            dialog.show()


    }

    private fun bluetoothDialog(title: TextView, webList: MutableList<String>): Boolean {
        title.text = context.getString(R.string.selectBluetooth)
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isEnabled) {
            val pairedDevices = bluetoothAdapter.bondedDevices
            pairedDevices.forEach { blue ->
                webList.add(blue.name)
            }
        } else {
            Toast.makeText(context, context.getString(R.string.turnOnBluetooth), Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }

    private fun wifiDialog(title: TextView, webList: MutableList<String>): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            title.text = context.getString(R.string.selectWifi)
            val wifiService: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiService.isWifiEnabled) {
                wifiService.configuredNetworks.forEach { web ->
                    webList.add(web.SSID.toString().substring(1, web.SSID.toString().length - 1))
                }
                wifiService.scanResults.forEach { web ->
                    webList.add(web.SSID)
                }
            } else {
                Toast.makeText(context, context.getString(R.string.turnOnWiFi), Toast.LENGTH_LONG).show()
                return true
            }
            return false
        }
        return false
    }

    private fun showTimeDialog() {
        val mcurrentTime = Calendar.getInstance()
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)

        val day = mcurrentTime.get(Calendar.DAY_OF_YEAR)
        val month = mcurrentTime.get(Calendar.MONTH)
        val year = mcurrentTime.get(Calendar.YEAR)

        val date = Calendar.getInstance()

        val datePicker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, pckYear, pckMonth, pckDay ->
            date.set(Calendar.DAY_OF_YEAR, pckDay)
            date.set(Calendar.MONTH, pckMonth + 1)
            date.set(Calendar.YEAR, pckYear)
            listener.timeCall(date, tag)
        }, year, month, day)

        val timePicker = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, pckHour, pckMinute ->
            date.set(Calendar.MINUTE, pckMinute)
            date.set(Calendar.HOUR_OF_DAY, pckHour)
            date.set(Calendar.SECOND, 0)
            datePicker.show()
        }, hour, minute, true)

        timePicker.show()
    }

    private fun getContactList(): List<String> {
        val cr = context.contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val contacts = mutableListOf<String>()

        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME))

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf<String>(id), null)
                    while (pCur!!.moveToNext()) {
                        contacts.add(name)
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
        return contacts
    }

}