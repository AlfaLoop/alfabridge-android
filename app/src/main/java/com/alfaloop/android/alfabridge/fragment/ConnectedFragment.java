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
import com.alfaloop.android.alfabridge.nest.NestService;


public class ConnectedFragment extends BaseBackFragment {
    public static final String TAG = ConnectedFragment.class.getSimpleName();
    public static ConnectedFragment newInstance() {
        return new ConnectedFragment();
    }

    // Nest Service
    private NestService mNestService;
    private BluetoothDevice mBluetoothDevice;

    private Toolbar mToolbar;
    private RelativeLayout mMainContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNestService = ((MainActivity)_mActivity).getNestService();
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
                pop();
            }
        });

        mMainContainer = (RelativeLayout) view.findViewById(R.id.main_container);
        TextView title = (TextView) mMainContainer.findViewById(R.id.title);
        TextView description = (TextView) mMainContainer.findViewById(R.id.description);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");

        // Stop discovery
        mNestService.disconnect(false);
        mNestService.stopDiscovery();
    }

    public void onSupportInvisible() {
        super.onSupportInvisible();
//        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
//        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        initDelayView();
    }

    private void initDelayView() {
    }



}
