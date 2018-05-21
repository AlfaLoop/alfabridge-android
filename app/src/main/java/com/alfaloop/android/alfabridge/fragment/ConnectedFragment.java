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
package com.alfaloop.android.alfabridge.fragment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfaloop.android.alfabridge.MainActivity;
import com.alfaloop.android.alfabridge.R;
import com.alfaloop.android.alfabridge.base.BaseBackFragment;
import com.alfaloop.android.alfabridge.db.model.ConnectionRecord;
import com.alfaloop.android.alfabridge.listener.TcpBridgeListener;
import com.alfaloop.android.alfabridge.nest.NestService;
import com.alfaloop.android.alfabridge.nest.NestTcpBridger;
import com.alfaloop.android.alfabridge.nest.event.BleCharacteristicWriteRequestEvent;
import com.alfaloop.android.alfabridge.nest.event.BleConnectionStateChangeEvent;
import com.alfaloop.android.alfabridge.nest.event.TcpConnectionEvent;
import com.alfaloop.android.alfabridge.nest.event.TcpMessageReceivedEvent;
import com.alfaloop.android.alfabridge.utility.ParserUtils;
import com.alfaloop.android.alfabridge.utility.SystemUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;


public class ConnectedFragment extends BaseBackFragment {
    public static final String TAG = ConnectedFragment.class.getSimpleName();
    public static ConnectedFragment newInstance() {
        return new ConnectedFragment();
    }

    // Nest Service
    private NestService mNestService;
    private boolean isConnected = false;
    private Toolbar mToolbar;
    private RelativeLayout mMainContainer;
    private NestTcpBridger mNestTcpBridger;

    private TextView mDescText;
    private TextView mTitleText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNestService = ((MainActivity)_mActivity).getNestService();
        mNestTcpBridger = NestTcpBridger.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Log.i(TAG, "initView");
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.toolbar_title_device_connected);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNestService.disconnect(false);
//                pop();
            }
        });

        mToolbar.setTitle(mNestService.getConnectedDevice().getAddress());

        mMainContainer = (RelativeLayout) view.findViewById(R.id.main_container);
        mTitleText = (TextView) mMainContainer.findViewById(R.id.title);
        mDescText = (TextView) mMainContainer.findViewById(R.id.description);

        String title = String.format("%s %s:%d", getResources().getString(R.string.listen_tcp),
                SystemUtils.getDeviceIpAddress(), NestTcpBridger.BRIDGE_PORT);
        mTitleText.setText(title);
        mDescText.setText(R.string.disconnect);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        EventBus.getDefault().register(this);

        mNestTcpBridger.setWatching(true);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        EventBus.getDefault().unregister(this);

        // Stop discovery
        mNestTcpBridger.setWatching(false);
        mNestService.stopDiscovery();
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectionStateChangeEvent(final BleConnectionStateChangeEvent event) {
        if (!event.isConnected()) {
            pop();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleCharacteristicWriteRequestEvent(final BleCharacteristicWriteRequestEvent event) {
        Log.v(TAG, event.toString());
        byte[] value = event.getValue();
        if (isConnected) {
            mNestTcpBridger.sendHexString(ParserUtils.bytesToHex(value));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTcpMessageReceivedEvent(final TcpMessageReceivedEvent event) {
        Log.i(TAG, "onMessageReceived: " + event.getMessage());
        mNestService.sendMessageByInboundChannel(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTcpConnectionEvent(final TcpConnectionEvent event) {
        if (event.isConnect()) {
            mDescText.setText(R.string.connected);
            isConnected = true;
        } else {
            mDescText.setText(R.string.disconnect);
            isConnected = false;
        }
    }
}
