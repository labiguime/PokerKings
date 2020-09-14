package com.games.pokerkings.data;

import android.util.Log;

import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DataSource {

    private Socket mSocket;

    public static final String GET_JOIN_GAME_AUTHORIZATION = "getJoinRoomAuthorization";
    public static final String GET_PRE_GAME_PLAYER_LIST = "getPreGamePlayerList";
    public static final String GET_READY_PLAYER_AUTHORIZATION = "getReadyPlayerAuthorization";
    public static final String GET_INITIAL_ROOM_DATA = "getInitialRoomData";
    public static final String GET_AUTHORIZATION_TO_PLAY = "getAuthorizationToPlay";
    public static final String GET_ROOM_STATE = "getRoomState";
    public static final String GET_ROOM_RESULTS = "getRoomResults";
    public static final String GET_DISCONNECT_EVENT = "getDisconnectEvent";

    private MutableLiveData<Result<TreeMap<String, User>>> preGamePlayerListLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Room>> joinGameAuthorizationLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Boolean>> readyPlayerAuthorizationLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<InitialGameDataResult>> initialRoomDataLiveData = new MutableLiveData<>();
    private MutableLiveData<Result<Boolean>> authorizationToPlayLiveData = new MutableLiveData<>();
    private MutableLiveData<RoomState> roomStateLiveData = new MutableLiveData<>();
    private MutableLiveData<RoomResults> roomResultsLiveData = new MutableLiveData<>();
    private MutableLiveData<DisconnectionType> disconnectEventLiveData = new MutableLiveData<>();


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
                Boolean success = data.getBoolean("success");
                if(!success) {
                    String message;
                    message = data.getString("message");
                    Result.Error result = new Result.Error(message);
                    joinGameAuthorizationLiveData.postValue(result);
                } else {
                    String spot;
                    String room;
                    spot = data.getString("spot");
                    room = data.getString("room");
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
                    Result.Success<Boolean> result = new Result.Success<>(true);
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

        mSocket.on(GET_DISCONNECT_EVENT, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                Integer type = data.getInt("type");
                if(type == 0) { // Disconnection from ready screen
                    Integer myIndex = data.getInt("my_index");
                    Integer numberOfPlayers = data.getInt("players_in_room");
                    Integer disconnectedPlayer = data.getInt("disconnected_player");
                    DisconnectionType dType = new DisconnectionType(type, myIndex, numberOfPlayers, disconnectedPlayer);
                    disconnectEventLiveData.postValue(dType);
                } else {
                    DisconnectionType dType = new DisconnectionType(type);
                    disconnectEventLiveData.postValue(dType);
                }
            } catch (JSONException e) {
                Log.d("DEBUG", e.getMessage());
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
                if(!success) {
                    String message = data.getString("message");
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

        mSocket.on(GET_ROOM_RESULTS, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String message = data.getString("message");
                Boolean hasRoundEnded = data.getBoolean("has_round_ended");
                Integer currentMinimum = data.getInt("current_minimum");
                Integer myIndex = data.getInt("my_index");
                Integer nPlayers = data.getInt("number_of_players");
                Integer gameStage = data.getInt("game_stage");
                Integer currentPlayer = data.getInt("current_player");
                Integer card1 = data.getInt("card_1");
                Integer card2 = data.getInt("card_2");
                Integer table1 = data.getInt("table_card_1");
                Integer table2 = data.getInt("table_card_2");
                Integer table3 = data.getInt("table_card_3");
                List<Integer> winner = null;
                List<Integer> allCards = null;
                List<Integer> playersMoney = null;
                List<Integer> listData = new ArrayList<>();

                JSONArray jArray = data.getJSONArray("winner");
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        listData.add(jArray.getInt(i));
                    }
                    winner = listData;
                }

                listData = new ArrayList<>();
                jArray = data.getJSONArray("all_cards");
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        listData.add(jArray.getInt(i));
                    }
                    allCards = listData;
                }

                listData = new ArrayList<>();
                jArray = data.getJSONArray("players_money");
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        listData.add(jArray.getInt(i));
                    }
                    playersMoney = listData;
                }

                RoomResults roomResults = new RoomResults(message, hasRoundEnded, currentMinimum, myIndex, nPlayers, gameStage, currentPlayer, card1, card2, table1, table2, table3, winner, allCards, playersMoney);
                roomResultsLiveData.postValue(roomResults);
            } catch (JSONException e) {
                roomResultsLiveData.postValue(new RoomResults(e.getMessage()));
            }
        });

        mSocket.on(GET_ROOM_STATE, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                Boolean hasRoundEnded = data.getBoolean("has_round_ended");
                Integer nextPlayer = data.getInt("next_player");
                Integer actionType = data.getInt("action_type");
                Integer whoPlayed = data.getInt("who_played");
                Integer playerNewMoney = data.getInt("player_new_money");
                Integer playerMoneyChange = data.getInt("player_money_change");
                Integer tableTotal = data.getInt("table_total");
                Integer tableCard = data.getInt("table_card");
                Integer currentMinimum = data.getInt("current_minimum");
                Integer myIndex = data.getInt("my_index");
                Integer nPlayers = data.getInt("number_of_players");
                Integer gameStage = data.getInt("game_stage");
                Boolean isGameOver = data.getBoolean("is_game_over");

                RoomState roomState = new RoomState(hasRoundEnded, nextPlayer, actionType, whoPlayed, playerNewMoney, playerMoneyChange, tableTotal, tableCard, currentMinimum, myIndex, nPlayers, isGameOver, gameStage);
                roomStateLiveData.postValue(roomState);
            } catch (JSONException e) {
                RoomState roomState = new RoomState(e.getMessage());
                roomStateLiveData.postValue(roomState);
            }
        });

    }

    public void postRequest(String req, JSONObject obj) {
        mSocket.emit(req, obj);
    }

    public void getRequest(String req, Emitter.Listener listener) {
        mSocket.on(req, listener);
    }

    public LiveData<RoomResults> onReceiveRoomResults() {
        return roomResultsLiveData;
    }

    public LiveData<Result<Boolean>> onReceiveAuthorizationToPlay() {
        return authorizationToPlayLiveData;
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

    public LiveData<DisconnectionType> onReceiveDisconnectEvent() {
        return disconnectEventLiveData;
    }

    public LiveData<RoomState> onReceiveRoomState() {
        return roomStateLiveData;
    }


}
