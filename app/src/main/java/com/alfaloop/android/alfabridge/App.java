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

import android.app.Application;
import android.util.Log;

import com.alfaloop.android.alfabridge.db.gen.DaoMaster;
import com.alfaloop.android.alfabridge.db.gen.DaoSession;

import org.greenrobot.greendao.database.Database;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

public class App extends Application {
    private final static String TAG = "App";
    private static final String DB_NAME = "greedDaoDemo.db";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);

        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        Fragmentation.builder()
        .stackViewMode(Fragmentation.BUBBLE)
        .debug(false)
        .handleException(new ExceptionHandler() {
            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.toString());
            }
        })
        .install();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


}
