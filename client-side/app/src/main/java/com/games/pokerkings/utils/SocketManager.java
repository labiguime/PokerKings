package com.games.pokerkings.utils;

import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketManager {
    private static Socket mSocket;
    public synchronized static Socket getInstance() {
        if(mSocket == null) {
            try {
                Log.d("DEBUG", "Trying to connect");
                mSocket = IO.socket("http://192.168.0.22:7000/");
                Log.d("DEBUG", "Setting the ip");
                mSocket.connect();
                Log.d("DEBUG", "Connecting");
            } catch (URISyntaxException e) {
                Log.d("DEBUG", "Failure");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return mSocket;
    }
}
