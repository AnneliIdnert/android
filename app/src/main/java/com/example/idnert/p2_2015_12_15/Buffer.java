package com.example.idnert.p2_2015_12_15;

import java.util.LinkedList;

/**
 * Created by idnert on 2015-12-11.
 */
public class Buffer {
    private LinkedList<String> buffer = new LinkedList<>();

    public synchronized void put(String element) {
        buffer.addLast(element);
        notifyAll();
    }

    public synchronized String get() {
        while (buffer.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return buffer.removeFirst();
    }
}
