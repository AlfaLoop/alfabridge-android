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
package com.alfaloop.android.alfabridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import com.alfaloop.android.alfabridge.base.BaseMainFragment;
import com.alfaloop.android.alfabridge.base.MySupportActivity;
import com.alfaloop.android.alfabridge.base.MySupportFragment;
import com.alfaloop.android.alfabridge.fragment.DevicesFragment;
import com.alfaloop.android.alfabridge.nest.NestService;
import com.alfaloop.android.alfabridge.utility.SystemUtils;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.List;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;


public class MainActivity  extends MySupportActivity
        implements  BaseMainFragment.OnFragmentOpenDrawerListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static List<MenuItem> mMenuItems;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mNavNameTextView;
    private TextView mNavEmailTextView;

    // NestService
    private NestService mNestService = null;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();

        SystemUtils.initDeviceName(this);
        // Bind to NestService
        Intent intent = new Intent(MainActivity.this, NestService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mBound) {
            mNestService.disconnect(true);
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove Listener
        //   FirebaseAuth auth = FirebaseAuth.getInstance();
        // auth.removeAuthStateListener(mFBAuthListener);
    }

    /**
     * 设置动画，也可以使用setFragmentAnimator()设置
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
        // 设置自定义动画
//        return new FragmentAnimator(enter,exit,popEnter,popExit);
    }

    private void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
//        mNavigationView.setNavigationItemSelectedListener(this);
//        mNavigationView.setCheckedItem(R.id.nav_devices);

        View header = mNavigationView.getHeaderView(0);
        mNavNameTextView = (TextView) header.findViewById(R.id.nav_name_text);
        mNavEmailTextView = (TextView) header.findViewById(R.id.nav_email_text);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
                mDrawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // goProfile();
                    }
                }, 250);
            }
        });
    }

    @Override
    public void onBackPressedSupport() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            ISupportFragment topFragment = getTopFragment();
            // 首頁的Fragment
            if (topFragment instanceof BaseMainFragment) {
//                mNavigationView.setCheckedItem(R.id.nav_devices);
            }
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                pop();
            } else {
                // PASS: 上ㄧ頁  (已無Fragment可pop) : into background?
                moveTaskToBack(true);
//                finish();
            }
        }
    }

    /**
     * 打開 Drawer
     */
    @Override
    public void onOpenDrawer() {
        if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Nest Service connection
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Nest service connected");
            mBound = true;
            NestService.LocalBinder binder = (NestService.LocalBinder) service;
            mNestService = binder.getService();
            MySupportFragment fragment = findFragment(DevicesFragment.class);
            if (fragment == null) {
                loadRootFragment(R.id.fragment_container, DevicesFragment.newInstance());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "Nest service disconnected");
            mBound = false;
        }
    };

    public NestService getNestService() {return mNestService; }
}

