package com.games.pokerkings.utils;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketManager {
    private static Socket mSocket;
    public synchronized static Socket getInstance() {
        if(mSocket == null) {
            try {
                mSocket = IO.socket("http://192.168.0.22:7000/");
                mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return mSocket;
    }
}
