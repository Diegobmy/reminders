package com.ragabuza.personalreminder.receivers

import android.app.Service
import android.content.Context
import android.location.LocationManager
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Bundle
import android.util.Log
import com.ragabuza.personalreminder.util.NotificationHelper


/**
 * Created by diego.moyses on 1/29/2018.
 */
class LocationReceiver : Service() {
    private var mLocationManager: LocationManager? = null

    private var mLocationListeners = arrayOf(LocationListener(LocationManager.GPS_PROVIDER), LocationListener(LocationManager.NETWORK_PROVIDER))

    inner class LocationListener(provider: String) : android.location.LocationListener {
        private var mLastLocation: Location = Location(provider)

        override fun onLocationChanged(location: Location) {
            mLastLocation.set(location)
            NotificationHelper(this@LocationReceiver).showNotification(0, "hello", "${location.latitude}/${location.longitude}")
        }

        override fun onProviderDisabled(provider: String) { }

        override fun onProviderEnabled(provider: String) {  }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {   }
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        initializeLocationManager()
        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[1])
        } catch (ex: java.lang.SecurityException) { } catch (ex: IllegalArgumentException) {    }

        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[0])
        } catch (ex: java.lang.SecurityException) { } catch (ex: IllegalArgumentException) {    }


    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {   }

            }
        }
    }

    private fun initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        }
    }

    companion object {
        private val TAG = "BOOMBOOMTESTGPS"
        private val LOCATION_INTERVAL = 10000
        private val LOCATION_DISTANCE = 0f
    }
}