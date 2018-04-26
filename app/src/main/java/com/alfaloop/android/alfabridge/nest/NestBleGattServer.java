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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.util.Log;

import com.alfaloop.android.alfabridge.nest.event.BleAdvertiseFailureEvent;
import com.alfaloop.android.alfabridge.nest.event.BleCharacteristicWriteRequestEvent;
import com.alfaloop.android.alfabridge.nest.event.BleConnectionStateChangeEvent;
import com.alfaloop.android.alfabridge.utility.ParserUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NestBleGattServer {
    public interface Listener {
        void onGattFinished();
    }

    private static final String TAG = NestBleGattServer.class.getSimpleName();
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mAdvertiser;
    private Listener mListener;
    private boolean mStarted = false;
    private boolean isAdvertising = false;
    private NestBleGattService mNestBleGattService = null;

    /* Collection of notification subscribers */
    private HashSet<BluetoothDevice> mRegisteredDevices = new HashSet<>();

    // Wrtie queue
    private Queue<byte[]>  mWriteQueue = new LinkedList<byte[]>();
    private boolean isWriting = false;
    private Context mContext;

    public NestBleGattServer(final Context context, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
        mBluetoothManager = (BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        this.mAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
    }

    public boolean start(AdvertiseSettings settings, AdvertiseData data) {
        if (mStarted)
            return false;

        mBluetoothGattServer = mBluetoothManager.openGattServer(this.mContext, mGattServerCallback);
        if (null == mBluetoothGattServer) {
            Log.e(TAG, "Failed to open GATT server");
            return false;
        }

        List<BluetoothGattService> gattServices = mBluetoothGattServer.getServices();
        for (BluetoothGattService service : gattServices) {
            if (service.getUuid() == NestBleGattService.NEST_SERVICE_UUID) {
                Log.e(TAG, "Another Nest-GATT service is already being served by this device");
                //close();

                // Start advertising.
                if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                    mAdvertiser.startAdvertising(settings, data, mAdvertisingCallback);
                    isAdvertising = true;
                } else {
                    Log.e(TAG, "multiple advertisiment not supported");
                }
                return false;
            }
        }

        mNestBleGattService = new NestBleGattService();
        if (!mBluetoothGattServer.addService(mNestBleGattService.getService())) {
            Log.e(TAG, "Nest-GATT service registration failed");
            //close();
            return false;
        }

        // Start advertising.
        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            mAdvertiser.startAdvertising(settings, data, mAdvertisingCallback);
            isAdvertising = true;
        } else {
            Log.e(TAG, "multiple advertisiment not supported");
        }
        return true;
    }

    public void stopAdvertising() {
        if (mBluetoothAdapter.isEnabled() && mAdvertiser != null) {
            // If stopAdvertising() gets called before close() a null
            // pointer exception is raised.
            mAdvertiser.stopAdvertising(mAdvertisingCallback);
        }
    }

    public void stop(BluetoothDevice device, boolean isCancelAll) {
        // clear service
        if (mBluetoothGattServer != null) {
            mBluetoothGattServer.clearServices();
            if (device != null) {
                Log.i(TAG, String.format("cancelConnection %s", device.getAddress()));
                mBluetoothGattServer.cancelConnection(device);
            }

            if (isCancelAll) {
                List<BluetoothDevice> devices = mBluetoothManager.getConnectedDevices( BluetoothGattServer.GATT);
                for(BluetoothDevice d : devices) {
                    if(d.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        Log.d(TAG, "Devices: " + device.getAddress() + " " + device.getName());
                        Log.i(TAG, String.format("getConnectedBLEDevices %s %s getConnectionState %d", d.getAddress(), device.getAddress(), mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT)));
                        mBluetoothGattServer.cancelConnection(d);
                    }
                }
            }
        }

        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            mAdvertiser.stopAdvertising(mAdvertisingCallback);
        } else {
            Log.e(TAG, "multiple advertisiment not supported");
        }
    }

    public void close(BluetoothDevice device) {
        if (mBluetoothGattServer != null) {
            Log.v(TAG, "clear services and close gatt server");
            mBluetoothGattServer.cancelConnection(device);
            mBluetoothGattServer.clearServices();
            mBluetoothGattServer.close();
            mBluetoothGattServer = null;
        }

        if (mBluetoothAdapter.isEnabled() && mAdvertiser != null) {
            // If stopAdvertising() gets called before close() a null
            // pointer exception is raised.
            mAdvertiser.stopAdvertising(mAdvertisingCallback);
        }

        // TODO: onGattFinished event
        if (this.mListener != null) {
            this.mListener.onGattFinished();
        }
    }

    public void writeInboundCharacteristic(BluetoothDevice device, byte[] value) {
        Log.d(TAG, "Write Inbound" + ParserUtils.parse(value));
        mWriteQueue.add(value);
        writeNextValueFromQueue(device);
    }

    private void writeNextValueFromQueue(BluetoothDevice device) {
//        if (mRegisteredDevices.isEmpty()) {
//            Log.i(TAG, "No subscribers registered");
//            return;
//        }
        if (isWriting) {
            return;
        }
        if (mWriteQueue.size() == 0) {
            return;
        }
        isWriting = true;
        byte[] value = mWriteQueue.poll();
        Log.d(TAG, "Write Inbound from Queue" + ParserUtils.parse(value));

//        BluetoothGattCharacteristic inboundCharacteristic =
//                mNestBleGattService.getService().getCharacteristic(NestBleGattService.NEST_INBOUND_CHARACTERISTIC_UUID);
//        inboundCharacteristic.setValue((byte[])value);

        mNestBleGattService.getInboundCharacteristic().setValue((byte[])value);
        if (mBluetoothGattServer != null && mNestBleGattService.getInboundCharacteristic() != null && device != null)
            mBluetoothGattServer.notifyCharacteristicChanged(device, mNestBleGattService.getInboundCharacteristic(), false);
    }

    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.v(TAG, "Notification sent. Status: " + status);
            isWriting = false;
            writeNextValueFromQueue(device);
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    Log.v(TAG, "Connected to device: " + device.getAddress());
                    mRegisteredDevices.add(device);
                    EventBus.getDefault().post(new BleConnectionStateChangeEvent(true, device));

                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.v(TAG, "Disconnected from device");
                    mRegisteredDevices.remove(device);
                    EventBus.getDefault().post(new BleConnectionStateChangeEvent(false, device));
                }
            } else {
                mRegisteredDevices.remove(device);
                Log.e(TAG, "Error when connecting: " + status);
            }
            Log.v(TAG, String.format("Devices Connected %d", mBluetoothManager.getConnectedDevices(BluetoothGattServer.GATT).size() ));
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.v(TAG, "Characteristic Write request: " + Arrays.toString(value));
            int status = BluetoothGatt.GATT_SUCCESS;
            if (offset != 0) {
                status = BluetoothGatt.GATT_INVALID_OFFSET;
            }

            // Measurement Interval is a 20 bytes characteristic
            if (value.length != 20) {
                status = BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
            }

            if (status == BluetoothGatt.GATT_SUCCESS) {
//                if (this.mListener != null) {
//                    this.mListener.onCharacteristicWriteRequest(device, value);
//                }
                EventBus.getDefault().post(new BleCharacteristicWriteRequestEvent(device, value));
            }

            if (responseNeeded) {
                if (mBluetoothGattServer != null)
                    mBluetoothGattServer.sendResponse(device, requestId, status,
                /* No need to respond with an offset */ 0,
                /* No need to respond with a value */ null);
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.d(TAG, "Device tried to read characteristic: " + characteristic.getUuid());
            Log.d(TAG, "Value: " + Arrays.toString(characteristic.getValue()));
            if (offset != 0) {
                if (mBluetoothGattServer != null)
                    mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
                /* value (optional) */ null);
                return;
            }
            if (mBluetoothGattServer != null)
                mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                        offset, characteristic.getValue());
        }

//    @Override
//    public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
////        super.onExecuteWrite(device, requestId, execute);
//        Log.v(TAG, String.format("%s Request %d: executeWrite(%s) is not expected!",
//                device, requestId, execute));
////        mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, new byte[0]);
//    }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
                                            BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.d(TAG, "Device tried to read descriptor: " + descriptor.getUuid());
            Log.d(TAG, "Value: " + Arrays.toString(descriptor.getValue()));

            if (offset != 0) {
                mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
            /* value (optional) */ null);
                return;
            }
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                    descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded,
                                             int offset,
                                             byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded,
                    offset, value);
            Log.v(TAG, "Descriptor Write Request " + descriptor.getUuid() + " " + Arrays.toString(value));
            int status = BluetoothGatt.GATT_SUCCESS;
            if (NestBleGattService.NEST_INBOUND_READ_CHARACTERISTIC_DESC.equals(descriptor.getUuid())) {
                BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                boolean supportsNotifications = (characteristic.getProperties() &
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
                boolean supportsIndications = (characteristic.getProperties() &
                        BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0;
                if (!(supportsNotifications || supportsIndications)) {
                    status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
                    Log.v(TAG, "Descriptor Write Request not supported");
                } else if (value.length != 2) {
                    status = BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
                    Log.v(TAG, "Descriptor Write Request invalid attribute length");
                } else if (Arrays.equals(value, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
                    status = BluetoothGatt.GATT_SUCCESS;
                    Log.v(TAG, "Descriptor Write Request disable notification success");
                    descriptor.setValue(value);
                } else if (supportsNotifications && Arrays.equals(value, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                    status = BluetoothGatt.GATT_SUCCESS;
                    Log.v(TAG, "Descriptor Write Request enable notification success");
                    descriptor.setValue(value);
                } else if (supportsIndications && Arrays.equals(value, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
                    status = BluetoothGatt.GATT_SUCCESS;
                    Log.v(TAG, "Descriptor Write Request enable indication success");
                    descriptor.setValue(value);
                } else {
                    Log.v(TAG, "Descriptor Write Request not supported");
                    status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
                }
            } else {
                status = BluetoothGatt.GATT_SUCCESS;
                Log.v(TAG, "Descriptor Write Request success");
                descriptor.setValue(value);
            }
            if (responseNeeded) {
                Log.v(TAG, "Descriptor Write Request request needed");
                mBluetoothGattServer.sendResponse(device, requestId, status,
            /* No need to respond with offset */ 0,
            /* No need to respond with a value */ null);
            }
        }

    };

    private AdvertiseCallback mAdvertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv="
                        + settingsInEffect.getTxPowerLevel()
                        + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.d(TAG, "onStartSuccess, settingInEffect is null");
            }
        }
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "Not broadcasting: " + errorCode);
            int statusText;
            switch (errorCode) {
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    Log.w(TAG, "App was already advertising");
                    EventBus.getDefault().post(new BleAdvertiseFailureEvent("App was already advertising", ADVERTISE_FAILED_ALREADY_STARTED));
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    Log.w(TAG, "data to large");
                    EventBus.getDefault().post(new BleAdvertiseFailureEvent("data to large", ADVERTISE_FAILED_DATA_TOO_LARGE));
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    Log.w(TAG, "feature unsupported");
                    EventBus.getDefault().post(new BleAdvertiseFailureEvent("feature unsupported", ADVERTISE_FAILED_FEATURE_UNSUPPORTED));
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    Log.w(TAG, "internal error");
                    EventBus.getDefault().post(new BleAdvertiseFailureEvent("internal error", ADVERTISE_FAILED_INTERNAL_ERROR));
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    EventBus.getDefault().post(new BleAdvertiseFailureEvent("too many advertisers", ADVERTISE_FAILED_TOO_MANY_ADVERTISERS));
                    Log.w(TAG, "too many advertisers");
                    break;
                default:
                    EventBus.getDefault().post(new BleAdvertiseFailureEvent("Unhandled error:", errorCode));
                    Log.wtf(TAG, "Unhandled error: " + errorCode);
            }
        }
    };
}
