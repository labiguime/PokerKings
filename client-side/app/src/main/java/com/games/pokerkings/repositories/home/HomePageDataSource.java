package com.games.pokerkings.repositories.home;

import com.games.pokerkings.models.User;
import com.games.pokerkings.utils.SocketManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

public class HomePageDataSource {

    private User user;
    private Socket mSocket;

    public HomePageDataSource() {
        this.user = new User();
        mSocket = SocketManager.getInstance();
    }

    public User getUser() {
        return this.user;
    }

    public void postRequest(String req, JSONObject obj) {
        mSocket.emit(req, obj);
    }

    public void getRequest(String req, Emitter.Listener listener) {
        mSocket.on(req, listener);
    }
}
