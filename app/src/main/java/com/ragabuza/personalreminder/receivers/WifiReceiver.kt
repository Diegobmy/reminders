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
//        val action = intent?.action
//        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//            val info = intent?.getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO) as NetworkInfo
//            val connected = info.isConnected
//            if (connected)
//                if (info.extraInfo == "\"Visitantes\"")
//                    NotificationHelper().showNotification(1, p0, 0, "nice")
//                else
//                    NotificationHelper().showNotification(1, p0, 0, info.extraInfo)
//            else
//                NotificationHelper().showNotification(1, p0, 0, "Not "+info.extraInfo)
//        }
        val mainWifi = p0?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList = mainWifi.scanResults
        var i = 1
        NotificationHelper().showNotification(0, p0, 0, "ok")
        wifiList.forEach { web ->
            NotificationHelper().showNotification(i, p0, 0, web.SSID.toString())
            i++
            if (web.SSID.toString().substring(1, web.SSID.toString().length - 1) == "CMOYSES")
                NotificationHelper().showNotification(1, p0, 0, "home")
            if (web.SSID.toString().substring(1, web.SSID.toString().length - 1) == "Visitantes")
                NotificationHelper().showNotification(1, p0, 0, "work")
        }
    }
}