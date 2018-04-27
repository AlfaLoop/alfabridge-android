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

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.alfaloop.android.alfabridge.fragment.DevicesFragment;
import com.alfaloop.android.alfabridge.listener.TcpBridgeListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;

public class NestTcpBridger {
    private static final String TAG = NestTcpBridger.class.getSimpleName();

    static final public int BRIDGE_PORT = 5897;
    private TcpBridgeListener mTcpBridgeListener;
    private ServerSocket mServerSocket;
    private Socket mSocket;
    private BufferedReader mBufferedReader;
    private BufferedWriter mBufferedWriter;
    private android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());

    public NestTcpBridger(TcpBridgeListener listener) {

        mTcpBridgeListener = listener;

        // Start the TCP Server
        TCPServerAsyncTask task = new TCPServerAsyncTask();
        task.execute(new Void[0]);
    }

    public void close() {
        try {
            if (mSocket != null)
                mSocket.close();
                mBufferedReader.close();
                mBufferedWriter.close();
            mServerSocket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendHexString(String str)
    {
        try
        {
            if (mBufferedWriter != null) {
                mBufferedWriter.write(str);
                mBufferedWriter.write("\r\n");
                mBufferedWriter.flush();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public class TCPServerAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private volatile boolean running = true;
        private volatile boolean disconnect = false;
        public TCPServerAsyncTask()
        {
        }

        @Override
        protected void onCancelled() {
            running = false;
            if (mTcpBridgeListener != null) {
                mTcpBridgeListener.onDisconnect();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mServerSocket = new ServerSocket(BRIDGE_PORT);
            } catch (IOException e) {
            }
            while (running) {
                try {
                    if (mTcpBridgeListener != null) {
                        mTcpBridgeListener.onDisconnect();
                    }
                    mSocket = mServerSocket.accept();
                    if (mTcpBridgeListener != null) {
                        mTcpBridgeListener.onConnected();
                    }
                    mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                    disconnect = false;
                    String line;
                    while( (line=mBufferedReader.readLine()) != null || disconnect)
                    {
                        if (disconnect) {
                            break;
                        } else {
                            if (line != null) {
                                final String message = line;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, message);
                                        if (mTcpBridgeListener != null) {
                                            mTcpBridgeListener.onMessageReceived(message);
                                        }
                                    }
                                });
                            }
                        }
                    }

                } catch (IOException e) {
                }
            }
            return null;
        }
    }
}
