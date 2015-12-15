package com.example.idnert.p2_2015_12_15;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idnert on 2015-12-15.
 */
public class Worker implements Runnable {
    private boolean run;
    private Buffer buffer;
    private MainActivity mainActivity;
    private Controller controller;

    public Worker(Buffer buffer, MainActivity mainActivity, Controller controller) {
        this.mainActivity = mainActivity;
        this.buffer = buffer;
        this.run = true;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (run) {
            try {
                String response = this.buffer.get();
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                switch (type) {
                    case "register":
                        this.register(response);
                        break;
                    case "groups":
                        this.groups(response);
                        break;
                    case "locations":
                        this.locations(response);
                        break;
                    case "location":
                        this.location(response);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void register(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            SharedPref.addId(mainActivity, jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void location(String response) {
    }

    private void locations(String response) {


        try {
            JSONObject jsonObject = new JSONObject(response);
            String group = jsonObject.getString("group");
            JSONArray locations = jsonObject.getJSONArray("location");
            List<LocationsMember> locationsMembers = new ArrayList<>();
            for (int i = 0; i < locations.length(); i++) {
                JSONObject location = locations.getJSONObject(i);
                double realLong = 0;
                double realLat = 0;
                String longitude = location.getString("longitude");
                String latitude = location.getString("latitude");
                if (longitude.equals("NaN")) {
                    realLong = 13.4567;
                } else {
                    realLong = Double.valueOf(longitude);
                }
                if (latitude.equals("NaN")) {
                    realLat = 56.4356;
                } else {
                    realLat = Double.valueOf(latitude);
                }
                locationsMembers.add(new LocationsMember(realLat, realLong, location.getString("member")));
            }
            controller.addLocations(group, locationsMembers);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    controller.notifyListeners();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void groups(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONArray groups = jsonObject.getJSONArray("groups");
            final List<String> groupsList = new ArrayList<>();
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.getJSONObject(i);
                groupsList.add(group.getString("group"));
            }

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setGroups(groupsList);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        this.run = false;
    }
}
