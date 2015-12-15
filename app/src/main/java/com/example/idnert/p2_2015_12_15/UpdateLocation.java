package com.example.idnert.p2_2015_12_15;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;

/**
 * Created by idnert on 2015-12-15.
 */
public class UpdateLocation implements Runnable {

    private double latitude;
    private double longitude;
    private boolean run;
    private LocationManager locationManager;
    private Context context;
    private Controller controller;
    private Buffer buffer;

    public UpdateLocation(LocationManager locationManager, Context context, Controller controller) {
        this.run = true;
        this.controller = controller;
        LL ll = new LL();
        this.context = context;
        this.buffer = new Buffer();
        this.locationManager = locationManager;
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, ll);
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, ll);
    }

    public void stop() {
        this.run = false;
    }

    @Override
    public void run() {
        while (run) {
            buffer.get();
            List<String> ids = SharedPref.getIds(context);
            if (ids != null) {
                if (ids.size() > 0) {
                    for (int i = 0; i < ids.size(); i++) {
                        controller.writeToServer(JSONGetter.postLocation(ids.get(i), longitude, latitude));
                    }
                }
            }
        }
    }

    private class LL implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            buffer.put("Update");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
