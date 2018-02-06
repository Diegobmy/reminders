package com.ragabuza.personalreminder.receivers

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.bluetooth.BluetoothProfile.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.widget.Toast
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.dao.WifiDAO
import com.ragabuza.personalreminder.triggers.BluetoothReminderTrigger
import com.ragabuza.personalreminder.triggers.WifiReminderTrigger
import com.ragabuza.personalreminder.util.NotificationHelper
import java.util.*


/**
 * Created by diego.moyses on 1/15/2018.
 */
class BluetoothReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {

        if (p0 != null) {
        val trigger = BluetoothReminderTrigger(p0)

        val device = intent?.extras?.get(EXTRA_DEVICE) as BluetoothDevice
        val status = intent.extras?.get(EXTRA_STATE) as Int

        when(status){
            STATE_CONNECTED -> trigger.connected(device.name)
            STATE_DISCONNECTED -> trigger.disconnected(device.name)
        }

        }
    }

}
