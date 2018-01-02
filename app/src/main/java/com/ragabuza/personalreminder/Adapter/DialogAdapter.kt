package com.ragabuza.personalreminder.adapter

import android.app.Dialog
import android.content.Context
import android.net.wifi.WifiManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.R.id.lv
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter



/**
 * Created by diego.moyses on 1/2/2018.
 */
class DialogAdapter(val context: Context, val type: String) {


    fun show(){

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_dialog, null)
        val lv = view.findViewById<ListView>(R.id.lv)
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        val title = view.findViewById<TextView>(R.id.tvTitle)

        view.findViewById<ImageButton>(R.id.ibClose).setOnClickListener {
            dialog.dismiss()
        }

        if (type == "W"){
            title.text = context.getString(R.string.selectWifi)
            val wifiService: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiService.configuredNetworks.forEach { web ->
                adapter.add(web.SSID.toString().substring(1,web.SSID.toString().length-1))
            }
            wifiService.scanResults.forEach { web ->
                adapter.add(web.SSID.toString().substring(1,web.SSID.toString().length-1))
            }
        } else if(type == "B"){
            title.text = context.getString(R.string.selectBluetooth)
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val pairedDevices = bluetoothAdapter.bondedDevices
            pairedDevices.forEach { blue ->
                adapter.add(blue.name)
            }
        }



        lv.adapter = adapter

        val filter = view.findViewById<EditText>(R.id.etFilter)

        filter.addTextChangedListener(object : TextWatcher{

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

        })

        dialog.setContentView(view)
        dialog.show()



    }

}