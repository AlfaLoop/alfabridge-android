package com.alfaloop.android.alfabridge.nest.socket;

import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;

import java.net.InetSocketAddress;

/**
 * Created by Chris on 2018/1/4.
 */

public class AirLogClient {
    private final String TAG = AirLogClient.class.getSimpleName();
    private static AirLogClient instance;
    private AsyncSocket mSocket;

    public static synchronized AirLogClient getInstance(){
        if(instance == null){
            instance = new AirLogClient();
        }
        return instance;
    }

    public AirLogClient() {
    }

    public void sendMessage(String message){
        Log.i(TAG, String.format("sendMessage"));
        if (mSocket != null) {
            Util.writeAll(mSocket, message.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) {
                        Log.i(TAG,"[Client] error wrote message");
                        mSocket = null;
                    }
                    else
                        Log.i(TAG,"[Client] Successfully wrote message");
                }
            });
        }
    }

    public void close(){
        if (mSocket != null)
            mSocket.close();
    }

    public void start(String ipAddr, int port) {
        AsyncServer.getDefault().connectSocket(new InetSocketAddress(ipAddr, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                Log.i(TAG, "onConnectCompleted");
                if(ex != null) throw new RuntimeException(ex);

                mSocket = socket;
                mSocket.setDataCallback(new DataCallback() {
                    @Override
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                        Log.i(TAG,"[Client] Received Message " + new String(bb.getAllByteArray()));
                    }
                });

                mSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        if(ex != null)
                            Log.i(TAG,"[Client] Error closed connection");
                        else
                            Log.i(TAG,"[Client] Successfully closed connection");
                        mSocket = null;
                    }
                });

                mSocket.setEndCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        if(ex != null)
                            Log.i(TAG,"[Client] error end connection");
                        else
                            Log.i(TAG,"[Client] Successfully end connection");
                        mSocket = null;
                    }
                });
            }
        });
    }
}
