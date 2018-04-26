/*
 *  Copyright (c) 2016 AlfaLoop Technology Co., Ltd. All Rights Reserved.
 *
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential.
 *
 *  Attribution - You must give appropriate credit, provide a link to the license, and
 *  indicate if changes were made. You may do so in any reasonable manner, but not in any
 *  way that suggests the licensor endorses you or your use.
 *
 *  NonCommercial - You may not use the material for commercial purposes under unauthorized.
 *
 *  NoDerivatives - If you remix, transform, or build upon the material, you may not
 *  distribute the modified material.
 */
package com.alfaloop.android.alfabridge.nest.event;

import android.bluetooth.BluetoothDevice;

import java.util.Arrays;

public class BleCharacteristicWriteRequestEvent {
    private byte[] value;
    private BluetoothDevice device;

    public BleCharacteristicWriteRequestEvent(BluetoothDevice device, byte[] value) {
        this.value = value;
        this.device = device;
    }

    public byte[] getValue() {
        return value;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "BleCharacteristicWriteRequestEvent{" +
                "value=" + Arrays.toString(value) +
                ", device=" + device +
                '}';
    }
}
