package com.example.idnert.p2_2015_12_15;

import android.content.Context;

/**
 * Created by idnert on 2015-12-15.
 */
public class JSONGetter {
    public static String getGroups() {
        return "{\"type\": \"groups\"}";
    }

    public static String createGroup(String groupName, Context context) {
        return "{\"type\": \"register\", \"new_group\": \"" + groupName + "\", \"member\": \"" + SharedPref.getUser(context) + "\"}";
    }

    public static String postLocation(String id, double longitude, double latitude) {
        return "{\"type\": \"location\" ,\"id\": \"" + id + "\", \"longitude\": \"" + longitude + "\", \"latitude\": \"" + latitude + "\"}";
    }

    public static String unregister(String id, Context context) {
        return "{\"type\": \"unregister\", \"id\": \"" + id + "\"}";
    }

}

