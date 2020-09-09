package com.games.pokerkings.data;

import android.util.Log;

import com.games.pokerkings.utils.SocketManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class DataSource {

    private Socket mSocket;

    public DataSource() {
        Log.d("DEBUG", "Create");
        mSocket = SocketManager.getInstance();
        Log.d("DEBUG", "Finish");
    }

    public void postRequest(String req, JSONObject obj) {
        mSocket.emit(req, obj);
    }

    public void getRequest(String req, Emitter.Listener listener) {
        mSocket.on(req, listener);
    }
}
