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

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import com.alfaloop.android.alfabridge.nest.event.BleCharacteristicWriteRequestEvent;
import com.alfaloop.android.alfabridge.utility.ParserUtils;
import com.alfaloop.android.alfabridge.utility.UuidUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import io.reactivex.subjects.PublishSubject;

public class NestService extends Service {
    private static final String TAG = NestService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();

    // Nest Bluetooth instance
    private NestBleGattServer mNestBleGattServer;
    private NestBluetoothDevice mNestBluetoothDevice;
    private BluetoothDevice mBluetoothDevice;

    public class LocalBinder extends Binder {
        public NestService getService() {
            return NestService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);
        mNestBluetoothDevice = null;

        // register callback
        mNestBleGattServer = new NestBleGattServer(this, new NestBleGattServer.Listener() {
            @Override
            public void onGattFinished() {
                // mark object as disposable
                mNestBleGattServer = null;   // close() not needed here
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        AirLogClient.getInstance().close();
        if (mNestBleGattServer != null) {
            Log.v(TAG, "close ble gatt server");
            if (mNestBluetoothDevice != null)
                mNestBleGattServer.close(mNestBluetoothDevice.getBleDevice());
            mNestBleGattServer = null;
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBleCharacteristicWriteRequestEvent(final BleCharacteristicWriteRequestEvent event) {
        Log.v(TAG, event.toString());
        byte[] value = event.getValue();
    }

    public void disconnect(boolean isCancelAll) {
        if (mNestBluetoothDevice == null) {
            Log.i(TAG, String.format("mConnectedBluetoothDevice is null"));
        } else {
            Log.i(TAG, String.format("Disconnect with %s", mNestBluetoothDevice.getBleDevice().getAddress()));
            mNestBleGattServer.stop(mNestBluetoothDevice.getBleDevice(), isCancelAll);
        }
    }

    public void setConnectedDevice(BluetoothDevice device) {
        this.mBluetoothDevice = device;
    }

    public BluetoothDevice getConnectedDevice() {
        return mBluetoothDevice;
    }

    public void startDirectFieldDiscovery(String macAddr){
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( true )
                .setTimeout(0)
                .build();

        // replace the last 6 bytes with mac address
        byte[] uuid = UuidUtils.asBytesFromUuid( NestBleGattService.NEST_SERVICE_UUID);
        byte[] macArray = ParserUtils.hexStringToByteArray(macAddr);
        uuid[10] = macArray[0];
        uuid[11] = macArray[1];
        uuid[12] = macArray[2];
        uuid[13] = macArray[3];
        uuid[14] = macArray[4];
        uuid[15] = macArray[5];
        ParcelUuid pUuid = new ParcelUuid( UuidUtils.asUuidFromByteArray(uuid));
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( false )
                .addServiceUuid( pUuid )
                .build();
        mNestBleGattServer.start(settings, data);
    }

    public void startNearFieldDiscovery(){
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( true )
                .setTimeout(0)
                .build();

        // pack the secure advertising 128bits UUID
        ParcelUuid pUuid = new ParcelUuid( NestBleGattService.NEST_SERVICE_UUID);
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( false )
                .addServiceUuid( pUuid )
                .build();

        mNestBleGattServer.start(settings, data);
    }

    public void startDeviceDiscovery(String macAddress){
        if (macAddress.equals("")) {
            startNearFieldDiscovery();
        } else {
            startDirectFieldDiscovery(macAddress);
        }
    }

    public void stopDiscovery(){
        mNestBleGattServer.stopAdvertising();
    }
}
