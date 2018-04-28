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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.alfaloop.android.alfabridge.nest.event.TcpConnectionEvent;
import com.alfaloop.android.alfabridge.nest.event.TcpMessageReceivedEvent;

import org.greenrobot.eventbus.EventBus;

public class NestTcpBridger {
    private static final String TAG = NestTcpBridger.class.getSimpleName();

    static final public int BRIDGE_PORT = 5897;
    private ServerSocket mServerSocket;
    private Socket mSocket;
    private BufferedReader mBufferedReader;
    private BufferedWriter mBufferedWriter;
    private TCPServerAsyncTask mTCPServerAsyncTask;

    public NestTcpBridger() {
        // Start the TCP Server
        mTCPServerAsyncTask = new TCPServerAsyncTask();
        mTCPServerAsyncTask.execute(new Void[0]);
    }

    public void close() {
        try {
            if (mSocket != null)
                mSocket.close();
            if (mServerSocket != null)
                mServerSocket.close();
            if (mBufferedReader != null)
                mBufferedReader.close();
            if (mBufferedWriter != null)
                mBufferedWriter.close();

            mTCPServerAsyncTask.cancel(true);
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
            EventBus.getDefault().post(new TcpConnectionEvent(false));
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mServerSocket = new ServerSocket(BRIDGE_PORT);
            } catch (IOException e) {
            }
            while (running) {
                try {
                    EventBus.getDefault().post(new TcpConnectionEvent(false));
                    mSocket = mServerSocket.accept();
                    EventBus.getDefault().post(new TcpConnectionEvent(true));
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
                                EventBus.getDefault().post(new TcpMessageReceivedEvent(message));
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
