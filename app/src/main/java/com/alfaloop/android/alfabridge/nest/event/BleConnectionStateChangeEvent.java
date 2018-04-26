package com.alfaloop.android.alfabridge.nest.event;

import android.bluetooth.BluetoothDevice;

/**
 * Created by chris on 2017/5/15.
 */

public class BleConnectionStateChangeEvent {
    boolean connected;
    BluetoothDevice device;

    public BleConnectionStateChangeEvent(boolean connected, BluetoothDevice device) {
        this.connected = connected;
        this.device = device;
    }

    public boolean isConnected() {
        return connected;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "BleConnectionStateChangeEvent{" +
                "connected=" + connected +
                ", device=" + device +
                '}';
    }
}
