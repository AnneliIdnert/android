package com.example.idnert.p2_2015_12_15;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by idnert on 2015-12-15.
 */
public class SharedPref { private final static String APP = "TEST";
    private final static String USER = "user";
    private final static String IDS_LIST = "ids";

    public synchronized static void setUser(Context context, String user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER, user);
        editor.commit();
    }

    public synchronized static String getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP, Activity.MODE_PRIVATE);
        if (sharedPreferences.contains(USER)) {
            String userReturn = sharedPreferences.getString(USER, null);
            return userReturn;
        }
        return null;
    }

    public synchronized static void addId(Context context, String id) {
        List<String> ids = getIds(context);
        boolean add = true;
        if (ids == null) {
            ids = new ArrayList<String>();
        } else {
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i).equals(id)) {
                    add = false;
                    break;
                }
            }
        }
        if (add) {
            ids.add(id);
            saveIds(context, ids);
        }
    }

    public synchronized static void deleteAllId(Context context) {
        saveIds(context, null);
    }

    public synchronized static void removeId(Context context, String id) {
        List<String> ids = getIds(context);
        if (ids != null) {
            if (ids.size() > 0) {
                for (int i = 0; i < ids.size(); i++) {
                    if (ids.get(i).equals(id)) {
                        ids.remove(i);
                        return;
                    }
                }
            }
        }
    }

    public synchronized static void saveLocations(Context context, List<LocationsMember> lms, String group) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String lmsString = gson.toJson(lms);
        editor.putString(group, lmsString);
        editor.commit();
    }

    public synchronized static List<LocationsMember> getLocations(Context context, String group) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(APP, Activity.MODE_PRIVATE);
        List<LocationsMember> lms;
        List<LocationsMember> lmsReal = new ArrayList<>();

        if (sharedPreferences.contains(group)) {
            String lmsString = sharedPreferences.getString(group, null);
            Gson gson = new Gson();
            LocationsMember[] userItems = gson.fromJson(lmsString, LocationsMember[].class);
            lms = Arrays.asList(userItems);
            for (int i = 0; i < lms.size(); i++) {
                lmsReal.add(lms.get(i));
            }
        } else {
            return null;
        }
        return lmsReal;
    }

    public synchronized static void saveIds(Context context, List<String> ids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String usersJSON = gson.toJson(ids);
        editor.putString(IDS_LIST, usersJSON);
        editor.commit();
    }

    public synchronized static List<String> getIds(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP, Activity.MODE_PRIVATE);
        List<String> ids;

        if (sharedPreferences.contains(IDS_LIST)) {
            String usersJSON = sharedPreferences.getString(IDS_LIST, null);
            if (usersJSON != null) {
                Gson gson = new Gson();
                String[] userItems = gson.fromJson(usersJSON, String[].class);
                if (userItems != null) {
                    ids = Arrays.asList(userItems);
                    ids = new ArrayList<String>(ids);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return ids;
    }
}
