package com.example.idnert.p2_2015_12_15;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements Controller.UpdateListener {

    private GoogleMap mMap;
    private Controller controller;
    private String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        Intent intent = getIntent();
        controller = Constants.controller;
        group = intent.getStringExtra("new_group");

        controller.setListener(this);

        setMarkers();
    }

    private void setMarkers() {
        List<LocationsMember> locationsMembers = controller.getLocations(group);

        if (locationsMembers != null) {


            for (int i = 0; i < locationsMembers.size(); i++) {
                LocationsMember locationsMember = locationsMembers.get(i);

                LatLng latLng = new LatLng(locationsMember.getLatitude(), locationsMember.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(locationsMember.getMember());
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_unregister) {
            List<String> ids = SharedPref.getIds(this);
            if (ids != null) {
                for (int i = 0; i < ids.size(); i++) {
                    String groupId = ids.get(i);
                    String[] idSplitted = groupId.split(",");
                    String idGroup = idSplitted[0];
                    if (group.equals(idGroup)) {
                        SharedPref.removeId(this, groupId);
                        new MyTask().execute(Constants.UNREGISTER, groupId);
                        finish();
                        break;
                    }
                }
            }
        } else if (id == R.id.action_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (id == R.id.action_satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (id == R.id.action_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.action_none) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        } else if (id == R.id.action_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onUpdate() {
        setMarkers();
    }

    private class MyTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String choice = params[0];

            switch (choice) {
                case Constants.GET_GROUPS:
                    controller.writeToServer(JSONGetter.unregister(params[1], MapsActivity.this));
                    break;
            }
            return null;
        }
    }
}
