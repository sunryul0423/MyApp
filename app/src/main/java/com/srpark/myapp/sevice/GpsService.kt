package com.srpark.myapp.sevice

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.srpark.myapp.utils.ServiceConstant.GPS_SERVICE_ID
import dagger.android.DaggerService

class GpsService : DaggerService() {

    private lateinit var locationManager: LocationManager
    private lateinit var gpsListener: GpsListener
    private var isRealTime = true

    private val mBinder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(GPS_SERVICE_ID, Notification())
    }

    /**
     * 서비스 바인더 Class
     */
    inner class LocalBinder : Binder() {

        fun getService(): GpsService {
            getLocation()
            return this@GpsService
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location?.let {
                gpsListener.getLocation(it)
                if (!isRealTime) {
                    locationManager.removeUpdates(this)
                }
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }

    private fun getLocation() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, locationListener)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, locationListener)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setGpsListener(gpsListener: GpsListener, isRealTime: Boolean = true) {
        this.isRealTime = isRealTime
        this.gpsListener = gpsListener
    }

    interface GpsListener {

        fun getLocation(location: Location)
    }
}