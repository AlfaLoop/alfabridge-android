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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.alfaloop.android.alfabridge.MainActivity;
import com.alfaloop.android.alfabridge.R;
import com.alfaloop.android.alfabridge.nest.event.BleAdvertiseFailureEvent;
import com.alfaloop.android.alfabridge.nest.event.BleCharacteristicWriteRequestEvent;
import com.alfaloop.android.alfabridge.nest.event.BleConnectionStateChangeEvent;
import com.alfaloop.android.alfabridge.nest.event.TcpConnectionEvent;
import com.alfaloop.android.alfabridge.nest.event.TcpMessageReceivedEvent;
import com.alfaloop.android.alfabridge.utility.ParserUtils;
import com.alfaloop.android.alfabridge.utility.UuidUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;

import io.reactivex.subjects.PublishSubject;

public class NestService extends Service {
    private static final String TAG = NestService.class.getSimpleName();

    public interface OnDiscoveryListener {
        void onConnected(BluetoothDevice device);
        void onFailed(final BleAdvertiseFailureEvent event);
    }

    public interface OnTcpBridgeListener {
        void onConnected();
        void onDisconnect();
        void onBleDisconnect();
    }

    private final IBinder mBinder = new LocalBinder();

    private OnDiscoveryListener mDiscoveryListener = null;
    private OnTcpBridgeListener mTcpBridgeListener = null;

    // Nest Bluetooth instance
    private NestBleGattServer mNestBleGattServer;
    private NestTcpBridger mNestTcpBridger;
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
        mBluetoothDevice = null;

        // register callback
        mNestBleGattServer = new NestBleGattServer(this, new NestBleGattServer.Listener() {
            @Override
            public void onGattFinished() {
                // mark object as disposable
                mNestBleGattServer = null;   // close() not needed here
            }
        });

        mNestTcpBridger =  NestTcpBridger.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mNestTcpBridger.close();
        if (mBluetoothDevice != null) {
            mBluetoothDevice = null;
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    public void registerTcpBridgeListener(OnTcpBridgeListener listener) {
        this.mTcpBridgeListener = listener;
    }

    public void unRegisterTcpBridgeListener() {
        this.mTcpBridgeListener = null;
    }

    public void disconnect(boolean isCancelAll) {
        if (mBluetoothDevice == null) {
            Log.i(TAG, String.format("mConnectedBluetoothDevice is null"));
        } else {
            mNestBleGattServer.stop(mBluetoothDevice, isCancelAll);
        }
    }

    public BluetoothDevice getConnectedDevice() {
        return mBluetoothDevice;
    }

    public boolean startDirectFieldDiscovery(String macAddr){
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
        return mNestBleGattServer.start(settings, data);
    }

    public boolean startNearFieldDiscovery(){
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

        return mNestBleGattServer.start(settings, data);
    }

    public boolean startDeviceDiscovery(String macAddress, OnDiscoveryListener listener){
        boolean result = false;
        mDiscoveryListener = listener;

        if (macAddress.equals("")) {
            result = startNearFieldDiscovery();
        } else {
            result = startDirectFieldDiscovery(macAddress);
        }
        return result;
    }

    public void stopDiscovery(){
        mDiscoveryListener = null;
        mNestBleGattServer.stopAdvertising();
    }

    public void sendMessageByInboundChannel(String hex) {
        if (mBluetoothDevice != null) {
            byte[] cmd = ParserUtils.hexStringToByteArray(hex);
            mNestBleGattServer.writeInboundCharacteristic(mBluetoothDevice, cmd);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectionStateChangeEvent(BleConnectionStateChangeEvent event) {
        Log.d(TAG, event.toString());
        if (event.isConnected()){
            mBluetoothDevice = event.getDevice();
            if (mDiscoveryListener != null) {
                mDiscoveryListener.onConnected(mBluetoothDevice);
            }
        } else {
            mBluetoothDevice = null;
            if (mTcpBridgeListener != null) {
                mTcpBridgeListener.onBleDisconnect();
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleAdvertiseFailureEvent(final BleAdvertiseFailureEvent event) {
        Log.d(TAG, event.toString());
        if (mDiscoveryListener != null) {
            mDiscoveryListener.onFailed(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBleCharacteristicWriteRequestEvent(final BleCharacteristicWriteRequestEvent event) {
        Log.v(TAG, event.toString());
        byte[] value = event.getValue();
        if (mBluetoothDevice != null) {
            mNestTcpBridger.sendHexString(ParserUtils.bytesToHex(value));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTcpMessageReceivedEvent(final TcpMessageReceivedEvent event) {
        Log.i(TAG, "onMessageReceived: " + event.getMessage());
        sendMessageByInboundChannel(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTcpConnectionEvent(final TcpConnectionEvent event) {
        if (mTcpBridgeListener != null) {
            if (event.isConnect()) {
                mTcpBridgeListener.onConnected();
            } else {
                mTcpBridgeListener.onDisconnect();
            }

        }
    }
}
