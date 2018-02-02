package com.ragabuza.personalreminder.receivers

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.ragabuza.personalreminder.dao.ReminderDAO
import com.ragabuza.personalreminder.triggers.LocationReminderTrigger
import com.ragabuza.personalreminder.util.NotificationHelper
import com.ragabuza.personalreminder.util.timeGet
import java.util.*
import android.content.BroadcastReceiver
import android.content.Context


class LocationReceiver : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private lateinit var mLocationClient: GoogleApiClient
    private var mLocationRequest = LocationRequest()

    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent(this, ServiceRestart::class.java)
        sendBroadcast(broadcastIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mLocationClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationRequest.interval = timeGet(60).minutes()
        mLocationRequest.fastestInterval = timeGet(1).minutes()


        val priority = LocationRequest.PRIORITY_HIGH_ACCURACY //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.priority = priority
        mLocationClient.connect()

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /*
     * LOCATION CALLBACKS
     */
    override fun onConnected(dataBundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this)
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    override fun onConnectionSuspended(i: Int) {
    }

    //to get the location change
    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            val checkLocDAO = ReminderDAO(this)
            val checkedLocations = checkLocDAO.getLocations()
            checkLocDAO.close()
            if (checkedLocations.isEmpty()) return

            val trigger = LocationReminderTrigger(this)

            checkedLocations.forEach {
                if (it.distanceTo(location) < 50)
                    trigger.inRange(it)
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }
}

class ServiceRestart : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        context.startService(Intent(context, LocationReceiver::class.java))
    }
}
