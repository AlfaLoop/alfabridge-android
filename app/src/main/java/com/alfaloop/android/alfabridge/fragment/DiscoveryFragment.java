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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfaloop.android.alfabridge.App;
import com.alfaloop.android.alfabridge.MainActivity;
import com.alfaloop.android.alfabridge.R;
import com.alfaloop.android.alfabridge.base.BaseBackFragment;
import com.alfaloop.android.alfabridge.db.gen.ConnectionRecordDao;
import com.alfaloop.android.alfabridge.db.gen.DaoSession;
import com.alfaloop.android.alfabridge.db.model.ConnectionRecord;
import com.alfaloop.android.alfabridge.nest.NestService;
import com.alfaloop.android.alfabridge.nest.event.BleAdvertiseFailureEvent;
import com.alfaloop.android.alfabridge.nest.event.BleConnectionStateChangeEvent;
import com.alfaloop.android.alfabridge.utility.ParserUtils;
import com.skyfishjy.library.RippleBackground;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;

public class DiscoveryFragment extends BaseBackFragment {
    public static final String TAG = DiscoveryFragment.class.getSimpleName();
    private static final String MACADDR = "macaddress";

    // Nest Service
    private NestService mNestService;

    // GUI component
    private Toolbar mToolbar;
    private TextView mModeText;
    private TextView mHintText;
    private RippleBackground mRippleBackground;

    private String mMacAddress;
    private ConnectionRecordDao mConnectionRecordDao;

    public static DiscoveryFragment newInstance(String macAddr) {
        DiscoveryFragment fragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MACADDR, macAddr);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
        }
        mMacAddress = getArguments().getString(MACADDR, "");
        mNestService = ((MainActivity)_mActivity).getNestService();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        initView(view);
        setFragmentAnimator(new DefaultHorizontalAnimator());
        return view;
    }

    private void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.toolbar_title_discovery_nearby);
        mModeText = (TextView) view.findViewById(R.id.discovery_mode);
        mHintText = (TextView) view.findViewById(R.id.discovery_hint);
        mRippleBackground = (RippleBackground) view.findViewById(R.id.ripple_background);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop discovery
                mNestService.stopDiscovery();
                pop();
            }
        });

        // Init Datas
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mConnectionRecordDao = daoSession.getConnectionRecordDao();
        mNestService = ((MainActivity)_mActivity).getNestService();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        initDelayView();
    }

    private void initDelayView() {
        // Auto start discover nearby devices
        startDiscoveryNearby();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Log.i(TAG, "onSupportVisible");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        Log.i(TAG, "onSupportInvisible");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onBackPressedSupport() {
        Log.i(TAG, "onBackPressedSupport");
        return super.onBackPressedSupport();
    }

    private void startDiscoveryNearby() {
        // start the counter timer for 30 seconds
        if (!mMacAddress.equals("")) {
            String macAddr = String.format("%s:%s:%s:%s:%s:%s", mMacAddress.substring(0 , 2),
                    mMacAddress.substring(2 , 4),
                    mMacAddress.substring(4 , 6),
                    mMacAddress.substring(6 , 8),
                    mMacAddress.substring(8 , 10),
                    mMacAddress.substring(10 , 12));
            mModeText.setText(String.format("Search for Device \n%s", macAddr));
            mHintText.setText(R.string.discovery_direct_hint);
        } else {
            mModeText.setText(R.string.discovery_nearby);
            mHintText.setText(R.string.discovery_nearby_hint);
        }
        mRippleBackground.startRippleAnimation();

        // Start Discovery
        mNestService.startDeviceDiscovery(mMacAddress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectionStateChangeEvent(final BleConnectionStateChangeEvent event) {
        BluetoothDevice device = event.getDevice();
        mNestService.setConnectedDevice(device);
        if (event.isConnected()) {
            String address = ParserUtils.asHexStringFromAddress(device.getAddress());
            ConnectionRecord record = new ConnectionRecord(null, address, new Date());
            mConnectionRecordDao.insertOrReplace(record);
            startWithPop(ConnectedFragment.newInstance());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleAdvertiseFailureEvent(final BleAdvertiseFailureEvent event) {
        if (event.getErrCode() == 3) {
            mNestService = ((MainActivity)_mActivity).getNestService();
            mNestService.stopDiscovery();
            startDiscoveryNearby();
        } else {
            new AlertDialog.Builder(_mActivity)
                    .setTitle(R.string.adv_failure_error)
                    .setMessage(R.string.adv_failure_reason_internal + " Error type: " + event.getErrMessage())
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }
}
