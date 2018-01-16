package com.ragabuza.personalreminder.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.wifi.WifiManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.text.method.TextKeyListener.clear
import android.view.View
import android.content.ContentValues.TAG
import android.provider.ContactsContract
import android.content.ContentResolver




/**
* Created by diego.moyses on 1/2/2018.
*/
class DialogAdapter(val context: Context, val type: String) {

    private val listener: OpDialogInterface = context as OpDialogInterface

    @SuppressLint("InflateParams")
    fun show(){

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_dialog, null)
        val lv = view.findViewById<ListView>(R.id.lv)
        val webList = mutableListOf<String>()
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val filter = view.findViewById<EditText>(R.id.etFilter)

        view.findViewById<ImageButton>(R.id.ibClose).setOnClickListener {
            dialog.dismiss()
        }

        if (type == "W"){
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
                return
            }
        } else if(type == "B"){
            title.text = context.getString(R.string.selectBluetooth)
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter.isEnabled) {
                val pairedDevices = bluetoothAdapter.bondedDevices
                pairedDevices.forEach { blue ->
                    webList.add(blue.name)
                }
            } else {
                Toast.makeText(context, context.getString(R.string.turnOnBluetooth), Toast.LENGTH_LONG).show()
                return
            }
        } else if (type == "CON"){
            webList.addAll(getContactList())
        } else if(type == "OWEB"){
            webList.add("Se conectou de")
            webList.add("Se desconectou de")
            filter.visibility = View.GONE
        } else if(type == "OLOC"){
            webList.add("Está em")
            webList.add("Não está em")
            filter.visibility = View.GONE
        }



        val linkedHashSet = LinkedHashSet<String>()
        linkedHashSet.addAll(webList)
        adapter.addAll(linkedHashSet)

        lv.adapter = adapter


        filter.addTextChangedListener(object : TextWatcher{

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) = Unit

        })

        dialog.setContentView(view)

        view.findViewById<ListView>(R.id.lv).setOnItemClickListener { _, iView, _, _ ->
            val item = iView.findViewById<TextView>(android.R.id.text1)
            if(type == "W")
                listener.wifiCall(item.text)
            else if(type == "B")
                listener.blueCall(item.text)
            else if(type == "OWEB"||type == "OLOC")
                listener.other(item.text)
            else if(type == "CON")
                listener.contactCall(item.text)

                dialog.dismiss()
        }

        dialog.show()



    }

    private fun getContactList():List<String> {
        val cr = context.contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val contacts = mutableListOf<String>()

        if ((if (cur != null) cur.count else 0) > 0) {
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
        if (cur != null) {
            cur.close()
        }
        return contacts
    }

}