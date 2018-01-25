package com.ragabuza.personalreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.ragabuza.personalreminder.util.NotificationHelper
import android.os.SystemClock
import android.text.format.DateFormat
import java.util.*
import android.content.SharedPreferences
import android.R.id.edit
import com.ragabuza.personalreminder.util.Shared


/**
 * Created by diego.moyses on 1/15/2018.
 */
class WifiReceiver : BroadcastReceiver() {


    override fun onReceive(p0: Context?, intent: Intent?) {

        val mainWifi = p0?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList = mainWifi.scanResults
        val preferences = Shared(p0)

        val oldWifi = preferences.getOldWifi()
        val newWifi = HashSet<String>()

        wifiList.forEach { web -> newWifi.add(web.SSID) }

        val checkedWifi = preferences.getCheckedWifi()

        checkedWifi.forEach {
            if (!oldWifi.contains(it) && newWifi.contains(it)) {
                NotificationHelper().showNotification(1, p0, 0, "Conectado")
            } else if (oldWifi.contains(it) && !newWifi.contains(it)) {
                NotificationHelper().showNotification(2, p0, 0, "Desconectado")
            }
        }

        preferences.setOldWifi(newWifi)

    }

}
