package com.camtittle.photosharing.engine.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.camtittle.photosharing.engine.common.result.Event
import com.google.android.gms.location.*

class LocationService {

    private val LOCATION_PERMISSION_REQ = 2

    private val _errors = MutableLiveData<Event<String>>()
    val errors: LiveData<Event<String>> = _errors

    private val _location = MutableLiveData<LatLong>()
    val location: LiveData<LatLong> = _location

    private lateinit var locationManager: FusedLocationProviderClient

    companion object {

        private lateinit var instance: LocationService

        fun getInstance(activity: Activity): LocationService {
            if (!this::instance.isInitialized) {
                instance = LocationService()
                instance.initialise(activity)
            }

            return instance
        }

        fun getOnlyBeansLocation(): LatLong {
            return LatLong(53.4788783, -2.2381378)
        }
    }

    fun initialise(activity: Activity) {
        locationManager = LocationServices.getFusedLocationProviderClient(activity)
        setupLocationClient(activity)
    }

    private fun setupLocationClient(activity: Activity) {
        checkLocationPermission(activity)

        val locationRequest = createLocationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            locationManager.requestLocationUpdates(locationRequest, object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        Log.d("LocationService", "Location: ${location.latitude}, ${location.longitude}")
                        _location.postValue(
                            LatLong(
                                location.latitude,
                                location.longitude
                        ))
                    }
                }

            }, Looper.getMainLooper())
        }

        task.addOnFailureListener {
            _errors.postValue(Event("Error getting location. Ensure location services are enabled and try again"))
        }
    }

    private fun checkLocationPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQ
            )
        }
    }

    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000
        mLocationRequest.fastestInterval = 60000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }


}