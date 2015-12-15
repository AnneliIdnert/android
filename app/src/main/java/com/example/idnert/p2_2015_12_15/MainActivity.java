package com.example.idnert.p2_2015_12_15;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity
        implements CustomClickListener {

    private ListView listGroups;
    private LocationManager locationManager;
    private UpdateLocation updateLocation;
    private Controller controller;
    private List<String> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listGroups = (ListView) findViewById(R.id.listView);

        registerListClick();

        controller = new Controller(this);

        Constants.controller = controller;

        new MyTask().execute(Constants.GET_GROUPS);

        registerUpdateLocationListener();

        checkIfUserExists();
    }

    private void checkIfUserExists() {
        String userName = SharedPref.getUser(this);

        if (userName == null) {
            NewUserFragmentDialog dialog = new NewUserFragmentDialog();
            dialog.setListener(this);
            dialog.show(getFragmentManager(), "NewUserDialog");
        }
    }

    private void registerUpdateLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (updateLocation == null) {
            updateLocation = new UpdateLocation(locationManager, this, controller);
            Thread thread = new Thread(updateLocation);
            thread.start();
        }
    }

    private void registerListClick() {
        listGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;

                new MyTask().execute(Constants.REGISTER, tv.getText().toString());
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("new_group", tv.getText().toString());

                startActivity(intent);
            }
        });
    }

    public void setGroups(List<String> groups) {
        if (groups != null) {
            listGroups.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groups));
            if (groups.size() == 0) {
                Toast.makeText(this, R.string.no_groups_available, Toast.LENGTH_SHORT).show();
            }
        }
        this.groups = groups;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateLocation.stop();
        controller.stopThreads();
        SharedPref.deleteAllId(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_group) {
            NewGroupFragmentDialog dialog = new NewGroupFragmentDialog();
            dialog.setListener(this);
            dialog.show(getFragmentManager(), "NewFragmentGroupDialog");
        } else if (id == R.id.action_update) {
            new MyTask().execute(Constants.GET_GROUPS);
        } else if (id == R.id.action_register_all) {
            new MyTask().execute(Constants.REGISTER_ALL);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(boolean save, String username) {
        if (save) {
            SharedPref.setUser(this, username);
        }
    }

    @Override
    public void newGroup(boolean save, String groupname) {
        if (save) {
            new MyTask().execute(Constants.CREATE_GROUP, groupname);
        }
    }

    private class MyTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String choice = params[0];

            switch (choice) {
                case Constants.GET_GROUPS:
                    controller.writeToServer(JSONGetter.getGroups());
                    break;
                case Constants.CREATE_GROUP:
                    controller.writeToServer(JSONGetter.createGroup(params[1], MainActivity.this));
                    controller.writeToServer(JSONGetter.getGroups());
                    break;
                case Constants.REGISTER_ALL:
                    for (int i = 0; i < groups.size(); i++) {
                        controller.writeToServer(JSONGetter.createGroup(groups.get(i), MainActivity.this));
                    }
                    break;
                case Constants.REGISTER:
                    controller.writeToServer(JSONGetter.createGroup(params[1], MainActivity.this));
                    break;
            }
            return null;
        }
    }
}
