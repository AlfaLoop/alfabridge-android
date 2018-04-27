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
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfaloop.android.alfabridge.App;
import com.alfaloop.android.alfabridge.R;
import com.alfaloop.android.alfabridge.MainActivity;
import com.alfaloop.android.alfabridge.adapter.DeviceAdapter;
import com.alfaloop.android.alfabridge.base.BaseMainFragment;
import com.alfaloop.android.alfabridge.base.RecyclerViewEmptySupport;
import com.alfaloop.android.alfabridge.db.gen.ConnectionRecordDao;
import com.alfaloop.android.alfabridge.db.gen.DaoSession;
import com.alfaloop.android.alfabridge.db.model.ConnectionRecord;
import com.alfaloop.android.alfabridge.listener.OnSwipeViewClickListener;
import com.alfaloop.android.alfabridge.nest.NestService;

import org.greenrobot.greendao.query.Query;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;

public class DevicesFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener, OnSwipeViewClickListener {
    private static final String TAG = DevicesFragment.class.getSimpleName();

    private NestService mNestService;
    private BluetoothDevice mBluetoothDevice;

    private Toolbar mToolbar;
    private DeviceAdapter mAdapter;
    private RecyclerViewEmptySupport mRecycleView;
    private View mEmptyView;

    private List<ConnectionRecord> mListValues = new ArrayList<>();
    private ConnectionRecordDao mConnectionRecordDao;
    private Query<ConnectionRecord> mConnectionRecordsQuery;

    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        initView(view);
        setFragmentAnimator(new DefaultHorizontalAnimator());
        return view;
    }

    private void initView(View view) {
        Log.i(TAG, "initView");
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.toolbar_title_deivce_list);
        mToolbar.inflateMenu(R.menu.fragment_devices);
        mToolbar.setOnMenuItemClickListener(this);

        mRecycleView = (RecyclerViewEmptySupport) view.findViewById(R.id.recycle_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mAdapter = new DeviceAdapter(_mActivity);
        mAdapter.setOnSwipeViewClickListener(this);
        mRecycleView.addItemDecoration(new DividerItemDecoration(_mActivity, DividerItemDecoration.VERTICAL));

        mRecycleView.setAdapter(mAdapter);
        mEmptyView = (View) view.findViewById(R.id.empty_view);
        mRecycleView.setEmptyView(mEmptyView);
        Button confirm = (Button) mEmptyView.findViewById(R.id.confirm);
        confirm.setText(R.string.card_first_time_connection_tip_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(DiscoveryFragment.newInstance(""));
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_connect_anim:
                setFragmentAnimator(new DefaultHorizontalAnimator());
                final PopupMenu popupMenu = new PopupMenu(_mActivity, mToolbar, GravityCompat.END);
                try {
                    Field[] fields = popupMenu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popupMenu);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popupMenu.inflate(R.menu.fragment_devices_pop);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_anim_near_discovery:
                                start(DiscoveryFragment.newInstance(""));
                                break;
                        }
                        popupMenu.dismiss();
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
        return true;
    }

    private void updateDeviceList() {
        mConnectionRecordsQuery = mConnectionRecordDao.queryBuilder().orderDesc(ConnectionRecordDao.Properties.UpdateDate).build();
        mListValues = mConnectionRecordsQuery.list();
        mAdapter.setDatas(mListValues);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        initDelayView();
    }

    private void initDelayView() {
        Log.i(TAG, "initDelayView");

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Log.i(TAG, "onSupportVisible");

        // Init Datas
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mConnectionRecordDao = daoSession.getConnectionRecordDao();
        updateDeviceList();
    }

    @Override
    public void onSurfaceClick(int position, View view) {
        Log.i(TAG, String.format("Surface Click position %d", position));

        final ConnectionRecord record = mListValues.get(position);
        mNestService = ((MainActivity)_mActivity).getNestService();
        startWithPop(DiscoveryFragment.newInstance(record.getMacAddr()));
    }

    @Override
    public void onSwipeDeleteClick(int position, View view) {
        mConnectionRecordsQuery = mConnectionRecordDao.queryBuilder().orderDesc(ConnectionRecordDao.Properties.UpdateDate).build();
        mListValues = mConnectionRecordsQuery.list();
        ConnectionRecord record = mListValues.get(position);
        Long id = record.getId();
        mConnectionRecordDao.deleteByKey(id);
        updateDeviceList();
    }
}
