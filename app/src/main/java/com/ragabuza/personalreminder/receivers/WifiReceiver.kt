package com.ragabuza.personalreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.dao.WifiDAO
import java.util.*


/**
 * Created by diego.moyses on 1/15/2018.
 */
class WifiReceiver : BroadcastReceiver() {


    override fun onReceive(p0: Context?, intent: Intent?) {

        val checkWifidao = ReminderDAO(p0)
        val checkedWifi = checkWifidao.getUniqueWifi()

        if (checkedWifi.isEmpty()) return

        val mainWifi = p0?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList = mainWifi.scanResults
        val oldWifidao = WifiDAO(p0)
        val trigger = WifiReminderTrigger(p0)

        val oldWifi = oldWifidao.get()
        val newWifi = HashSet<String>()

        wifiList.forEach { web -> newWifi.add(web.SSID) }

        checkedWifi.forEach {
            if (!oldWifi.contains(it) && newWifi.contains(it)) {
                trigger.connected(it)
            } else if (oldWifi.contains(it) && !newWifi.contains(it)) {
                trigger.disconnected(it)
            }
        }

        oldWifidao.add(newWifi)
        oldWifidao.close()
        checkWifidao.close()

    }

}
