package com.games.pokerkings.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.data.models.Room;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.utils.Result;
import com.games.pokerkings.utils.SocketManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.TreeMap;

public class DataSource {

    private Socket mSocket;

    public static final String GET_JOIN_GAME_AUTHORIZATION = "getJoinRoomAuthorization";
    public static final String GET_PRE_GAME_PLAYER_LIST = "getPreGamePlayerList";
    public static final String GET_READY_PLAYER_AUTHORIZATION = "getReadyPlayerAuthorization";

    private MutableLiveData<Result<TreeMap<String, User>>> preGamePlayerListLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Room>> joinGameAuthorizationLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Boolean>> readyPlayerAuthorizationLiveData = new MutableLiveData<>();

    public DataSource() {
        mSocket = SocketManager.getInstance();

        mSocket.on(GET_PRE_GAME_PLAYER_LIST, args -> {
            JSONObject data = (JSONObject) args[0];
            HashMap<String, User> fetchedUsers = new HashMap<>();
            try {
                JSONArray array = data.getJSONArray("players");

                for(int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    fetchedUsers.put(obj.getString("spot_id"), new User(obj.getString("name"), obj.getString("avatar"), obj.getBoolean("ready")));
                }

                TreeMap<String, User> sortUsers = new TreeMap<>(fetchedUsers);

                Result.Success<TreeMap<String, User>> result = new Result.Success<>(sortUsers);
                preGamePlayerListLiveData.postValue(result);

            } catch (JSONException e) {
                Result.Error result = new Result.Error(e.getMessage());
                preGamePlayerListLiveData.postValue(result);
            }

        });

        mSocket.on(GET_JOIN_GAME_AUTHORIZATION, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                Boolean success;
                String message;
                String spot;
                String room;

                success = data.getBoolean("success");
                message = data.getString("message");
                spot = data.getString("spot");
                room = data.getString("room");

                if(!success) {
                    Result.Error result = new Result.Error(message);
                    joinGameAuthorizationLiveData.postValue(result);
                } else {
                    Result.Success<Room> result = new Result.Success<>(new Room(room, spot));
                    joinGameAuthorizationLiveData.postValue(result);
                }

            } catch (JSONException e) {
                Result.Error result = new Result.Error(e.getMessage());
                joinGameAuthorizationLiveData.postValue(result);
            }
        });

    }

    public void postRequest(String req, JSONObject obj) {
        mSocket.emit(req, obj);
    }

    public void getRequest(String req, Emitter.Listener listener) {
        mSocket.on(req, listener);
    }

    public LiveData<Result<TreeMap<String, User>>> onReceivePreGamePlayerList() {
        return preGamePlayerListLiveData;
    }

    public LiveData<Result<Room>> onReceiveJoinGameAuthorization() {
        return joinGameAuthorizationLiveData;
    }

    public LiveData<Result<Boolean>> onReceiveReadyPlayerAuthorization() {
        return readyPlayerAuthorizationLiveData;
    }


}
