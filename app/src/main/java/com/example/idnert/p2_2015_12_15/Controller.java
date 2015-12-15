package com.example.idnert.p2_2015_12_15;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * Created by idnert on 2015-12-10.
 */
public class Controller implements Serializable {

    private MainActivity mainActivity;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Buffer buffer;
    private HashMap<String, List<LocationsMember>> locations;

    private Worker worker;
    private Listener listener;

    private UpdateListener updateListener;


    public void setListener(UpdateListener listener) {
        this.updateListener = listener;
    }

    public void notifyListeners() {
        if (updateListener != null) {
            updateListener.onUpdate();
        }
    }

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.buffer = new Buffer();
        this.locations = new HashMap<>();

        Thread t = new Thread(new InitSocket());
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        worker = new Worker(buffer, mainActivity, this);
        listener = new Listener(buffer, dataInputStream);

        startThreads();
    }

    public void addLocations(String group, List<LocationsMember> members) {
        synchronized (locations) {
            locations.put(group, members);
        }
    }

    public List<LocationsMember> getLocations(String group) {
        synchronized (locations) {
            return locations.get(group);
        }
    }

    public synchronized void writeToServer(String json) {
        try {
            dataOutputStream.writeUTF(json);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startThreads() {
        new Thread(listener).start();
        new Thread(worker).start();
    }

    public void initSocket() {
        try {
            Socket socket = new Socket("195.178.232.7", 7117);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopThreads() {
        worker.stop();
        listener.stop();
    }

    private class InitSocket implements Runnable {

        @Override
        public void run() {
            initSocket();
        }
    }

    public interface UpdateListener {
        void onUpdate();
    }

}
