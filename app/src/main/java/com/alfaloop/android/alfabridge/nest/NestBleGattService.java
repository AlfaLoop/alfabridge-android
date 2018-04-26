/**
 * © Copyright AlfaLoop Technology Co., Ltd. 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.alfaloop.android.alfabridge.nest;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class NestBleGattService {

    /** Nest service UUID */
    public final static UUID NEST_SERVICE_UUID = UUID.fromString("af731700-347c-fe94-1700-8295a1e42b09");

    /** Nest Inbound characteristics */
    public static final UUID NEST_INBOUND_CHARACTERISTIC_UUID = UUID.fromString("af731701-347c-fe94-1700-8295a1e42b09");
    public static final UUID NEST_INBOUND_READ_CHARACTERISTIC_DESC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /** Nest Outbound characteristics */
    public static final UUID NEST_OUTBOUND_CHARACTERISTIC_UUID = UUID.fromString("af731702-347c-fe94-1700-8295a1e42b09");

    private BluetoothGattService mService;
    private BluetoothGattCharacteristic mNestInboundCharacteristic, mNestOutboundCharacteristic;

    public NestBleGattService() {
        mService = new BluetoothGattService(NEST_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        mNestInboundCharacteristic =
                new BluetoothGattCharacteristic(NEST_INBOUND_CHARACTERISTIC_UUID,
                        //Read-only characteristic, supports notifications
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ);

        BluetoothGattDescriptor nestInboundDesc = new BluetoothGattDescriptor(NEST_INBOUND_READ_CHARACTERISTIC_DESC,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        mNestInboundCharacteristic.addDescriptor(nestInboundDesc);

        mNestOutboundCharacteristic =
                new BluetoothGattCharacteristic(NEST_OUTBOUND_CHARACTERISTIC_UUID,
                        //write permissions
                        BluetoothGattCharacteristic.PROPERTY_WRITE , BluetoothGattCharacteristic.PERMISSION_WRITE);

        mService.addCharacteristic(mNestInboundCharacteristic);
        mService.addCharacteristic(mNestOutboundCharacteristic);
    }

    public BluetoothGattService getService() {
        return mService;
    }

    public BluetoothGattCharacteristic getInboundCharacteristic() {
        return mNestInboundCharacteristic;
    }

    public BluetoothGattCharacteristic getOutboundCharacteristic() {
        return mNestOutboundCharacteristic;
    }

}
