package com.fazevaib.shushit;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class MyTrackingService extends Service {

    public static final String TAG = "TAG";
    LocationManager locationManager = null;


    public MyTrackingService() {
    }

    private class LocationListener implements android.location.LocationListener {
        Location lastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            lastLocation = new Location(provider);
        }


        @Override
        public void onLocationChanged(Location location) {

            Log.e("TAG", "onLocationChanged: " + location);
            lastLocation.set(location);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.e(TAG, "onStatusChanged: " + s);

        }

        @Override
        public void onProviderEnabled(String s) {
            Log.e("TAG", "onProviderEnabled: " + s);

        }

        @Override
        public void onProviderDisabled(String s) {

            Log.e("TAG", "onProviderDisabled: " + s);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


    LocationListener[] locationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate:");
        initializeLocationManager();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000, 10f, locationListeners[1]);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                10f, locationListeners[0]);

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (locationManager != null) {
            for (int i = 0; i < locationListeners.length; i++) {
                try {
                    locationManager.removeUpdates(locationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
