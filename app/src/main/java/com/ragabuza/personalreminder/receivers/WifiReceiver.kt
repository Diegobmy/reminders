package com.ragabuza.personalreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Parcelable
import com.ragabuza.personalreminder.util.NotificationHelper
import java.nio.file.Files.size


/**
 * Created by diego.moyses on 1/15/2018.
 */
class WifiReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        val mainWifi = p0?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList = mainWifi.scanResults
        NotificationHelper().showNotification(1, p0, 0, wifiList.toString())
        wifiList.forEach { web ->
            if (web.SSID == "CMOYSES")
                NotificationHelper().showNotification(1, p0, 0, "home")
            if (web.SSID == "Visitantes")
                NotificationHelper().showNotification(1, p0, 0, "work")
        }
    }
}