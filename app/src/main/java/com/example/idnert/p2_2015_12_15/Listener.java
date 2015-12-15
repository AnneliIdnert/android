package com.example.idnert.p2_2015_12_15;

import org.json.JSONObject;

import java.io.DataInputStream;

/**
 * Created by idnert on 2015-12-15.
 */
public class Listener implements Runnable {

    private boolean run;
    private Buffer buffer;
    private DataInputStream dataInputStream;

    public Listener(Buffer buffer, DataInputStream dataInputStream) {
        this.buffer = buffer;
        this.dataInputStream = dataInputStream;
        this.run = true;
    }

    @Override
    public void run() {
        while (run) {
            try {
                String response = dataInputStream.readUTF();
                JSONObject jsonObject = new JSONObject(response);
                String type = jsonObject.getString("type");
                switch (type) {
                    case "locations":
                    case "location":
                    case "register":
                    case "groups":
                    case "unregister":
                        buffer.put(response);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.run = false;
    }
}
