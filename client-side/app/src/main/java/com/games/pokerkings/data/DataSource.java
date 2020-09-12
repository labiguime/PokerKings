package com.games.pokerkings.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.data.models.Room;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.utils.Constants;
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
    public static final String GET_INITIAL_ROOM_DATA = "getInitialRoomData";
    public static final String GET_AUTHORIZATION_TO_PLAY = "getAuthorizationToPlay";
    public static final String GET_ROOM_STATE = "getRoomState";

    private MutableLiveData<Result<TreeMap<String, User>>> preGamePlayerListLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Room>> joinGameAuthorizationLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Boolean>> readyPlayerAuthorizationLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<InitialGameDataResult>> initialRoomDataLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Boolean>> authorizationToPlayLiveData = new MutableLiveData<>();


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

        mSocket.on(GET_READY_PLAYER_AUTHORIZATION, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                Boolean success = data.getBoolean("success");
                if(success) {
                    Result.Success<Boolean> result = new Result.Success<>(success);
                    readyPlayerAuthorizationLiveData.postValue(result);
                } else {
                    Result.Error result = new Result.Error(Constants.ERROR_UNKNOWN);
                    readyPlayerAuthorizationLiveData.postValue(result);
                }

            } catch (JSONException e) {
                Result.Error result = new Result.Error(e.getMessage());
                readyPlayerAuthorizationLiveData.postValue(result);
            }
        });

        mSocket.on(GET_INITIAL_ROOM_DATA, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                Integer userIndex = data.getInt("my_index");
                Integer numberOfPlayers = data.getInt("number_of_players");
                Integer currentMinimum = data.getInt("current_minimum");
                Integer currentPlayerIndex = data.getInt("current_player");
                Integer startMoney = data.getInt("start_money");
                Integer card1 = data.getInt("card_1");
                Integer card2 = data.getInt("card_2");
                Integer table1 = data.getInt("table_card_1");
                Integer table2 = data.getInt("table_card_2");
                Integer table3 = data.getInt("table_card_3");
                Result.Success<InitialGameDataResult> result = new Result.Success<>(new InitialGameDataResult(true, userIndex, numberOfPlayers, currentMinimum, currentPlayerIndex, startMoney, card1, card2, table1, table2, table3));
                initialRoomDataLiveData.postValue(result);
            } catch (JSONException e) {
                Result.Error result = new Result.Error(e.getMessage());
                initialRoomDataLiveData.postValue(result);
            }
        });

        mSocket.on(GET_AUTHORIZATION_TO_PLAY, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                Boolean success = data.getBoolean("success");
                String message = data.getString("message");

                if(!success) {
                    Result.Error result = new Result.Error(message);
                    authorizationToPlayLiveData.postValue(result);
                } else {
                    Result.Success<Boolean> result = new Result.Success<>(true);
                    authorizationToPlayLiveData.postValue(result);
                }
            } catch (JSONException e) {
                Result.Error result = new Result.Error(e.getMessage());
                authorizationToPlayLiveData.postValue(result);
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

    public LiveData<Result<InitialGameDataResult>> onReceiveInitialRoomData() {
        return initialRoomDataLiveData;
    }


}
