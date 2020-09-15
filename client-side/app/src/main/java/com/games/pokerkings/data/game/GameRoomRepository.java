package com.games.pokerkings.data.game;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.DisconnectionType;
import com.games.pokerkings.data.InitialGameDataResult;
import com.games.pokerkings.data.RoomResults;
import com.games.pokerkings.data.RoomState;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.utils.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class GameRoomRepository {

    private static volatile GameRoomRepository instance;
    private DataSource dataSource;
    private User user;
    private RoomResults roomResults;
    private MutableLiveData<Boolean> hasUserInterfaceLoaded;
    private MutableLiveData<Boolean> hasGameStarted;
    private MutableLiveData<Boolean> isPlayerTurn;
    private MutableLiveData<List<String>> avatarType;
    private MutableLiveData<List<String>> avatar;
    private MutableLiveData<List<String>> name;
    private MutableLiveData<List<String>> money;
    private MutableLiveData<Result.Error> notifyReadyPlayerError;
    private MutableLiveData<Result<Boolean>> notifyOnPlayError;
    private MediatorLiveData<Result<Boolean>> readyPlayerAuthorizationListener;
    private MediatorLiveData<Boolean> preGamePlayerListListener;
    private MediatorLiveData<InitialGameDataResult> initialGameDataListener;
    private MediatorLiveData<RoomResults> roomResultsListener;
    private MediatorLiveData<RoomState> roomStateListener;
    private MediatorLiveData<Result<Boolean>> authorizationToPlayListener;
    private MutableLiveData<Integer> totalMoney;
    private MutableLiveData<Integer> currentMinimum;
    private MutableLiveData<List<Integer>> tableCards;
    private MutableLiveData<List<Integer>> playerCards;
    private LiveData<DisconnectionType> disconnectEventListener;
    private Integer currentMinimumLocal;

    public static final String TAG = "LOG_GAME_ROOM";

    public GameRoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.user = new User();
        notifyReadyPlayerError = new MutableLiveData<>();
        notifyOnPlayError = new MutableLiveData<>();
        readyPlayerAuthorizationListener = new MediatorLiveData<>();
        preGamePlayerListListener = new MediatorLiveData<>();
        initialGameDataListener = new MediatorLiveData<>();
        roomResultsListener = new MediatorLiveData<>();
        roomStateListener = new MediatorLiveData<>();
        authorizationToPlayListener = new MediatorLiveData<>();
        isPlayerTurn = new MutableLiveData<>(false);
        hasGameStarted = new MutableLiveData<>(false);
        tableCards = new MutableLiveData<>(Arrays.asList(-1, -1, -1, -1, -1));
        this.roomResults = new RoomResults("No results");
        playerCards = new MutableLiveData<>(Arrays.asList(-1, -1));
        hasUserInterfaceLoaded = new MutableLiveData<>(false);
        totalMoney = new MutableLiveData<>();
        currentMinimum = new MutableLiveData<>();
        currentMinimumLocal = 0;
        avatarType = new MutableLiveData<>(Arrays.asList("", "", "", ""));
        avatar = new MutableLiveData<>(Arrays.asList("", "", "", ""));
        name = new MutableLiveData<>(Arrays.asList("", "", "", ""));
        money = new MutableLiveData<>(Arrays.asList("", "", "", ""));

        this.preGamePlayerListListener.addSource(dataSource.onReceivePreGamePlayerList(), this::processPreGamePlayerList);
        this.readyPlayerAuthorizationListener.addSource(dataSource.onReceiveReadyPlayerAuthorization(), this::processReadyPlayerAuthorization);
        this.initialGameDataListener.addSource(dataSource.onReceiveInitialRoomData(), this::processInitialGameData);
        this.roomStateListener.addSource(dataSource.onReceiveRoomState(), this::processRoomState);
        this.roomResultsListener.addSource(dataSource.onReceiveRoomResults(), this::processRoomResults);
        this.readyPlayerAuthorizationListener.addSource(notifyReadyPlayerError, value -> readyPlayerAuthorizationListener.setValue(value));
        this.authorizationToPlayListener.addSource(dataSource.onReceiveAuthorizationToPlay(), value -> authorizationToPlayListener.setValue(value));
        this.authorizationToPlayListener.addSource(notifyOnPlayError, value -> authorizationToPlayListener.setValue(value));
        this.disconnectEventListener = Transformations.map(dataSource.onReceiveDisconnectEvent(), value -> {
            processDisconnectEvent(value);
            return value;
        });
    }

    public LiveData<Boolean> onReceivePreGamePlayerList() {
        return preGamePlayerListListener;
    }

    public LiveData<Result<Boolean>> onReceiveReadyPlayerAuthorization() {
        return readyPlayerAuthorizationListener;
    }

    public LiveData<InitialGameDataResult> onReceiveInitialGameData() {
        return initialGameDataListener;
    }

    public LiveData<Result<Boolean>> onReceiveAuthorizationToPlay() {
        return authorizationToPlayListener;
    }

    public LiveData<RoomResults> onReceiveRoomResults() {
        return roomResultsListener;
    }

    public LiveData<RoomState> onReceiveRoomState() {
        return roomStateListener;
    }

    public LiveData<DisconnectionType> onReceiveDisconnectEvent() {
        return disconnectEventListener;
    }

    public void processDisconnectEvent(DisconnectionType t) {
        if(t.getType() == 0) {
            Integer startingPlayerIndex = getLayoutForId(t.getMyIndex(), t.getDisconnectedPlayer(), t.getNumberOfPlayers());
            ListManipulation.set(avatarType, startingPlayerIndex, "",false);
            for(int i = 0; i < 4; i++) {
                ListManipulation.set(money, i, "NOT READY",false);
            }
            hasGameStarted.setValue(false);
            initialGameDataListener.setValue(new InitialGameDataResult(""));

        }
        return;
    }

    public static GameRoomRepository getInstance() {
        return new GameRoomRepository(new DataSource());
    }

    public void loadGamePageComponents(User user) {
        /* Set the user variable and UI */
        this.user = user;
        setDefaultLiveDataForUser(0, user, false);

        /* Construct an object that we will post to socket.io */
        JSONObject object = new JSONObject();
        try {
            object.put("room_id", user.getRoom().getName());
        } catch(JSONException e) {

        }
        dataSource.postRequest("room/GET:preGamePlayerList", object);

    }

    public void alertPlayerReady() {

        String roomId;
        JSONObject object = new JSONObject();
        try {
            roomId = user.getRoom().getName();
        } catch (NullPointerException e) {
            notifyReadyPlayerError.setValue(new Result.Error(e.getMessage()));
            return;
        }

        // TODO: This request should take user id or spot id instead of name
        try {
            object.put("room_id", roomId);
            object.put("name", user.getName());
        } catch(JSONException e) {
            notifyReadyPlayerError.setValue(new Result.Error(e.getMessage()));
            return;
        }

        dataSource.postRequest("room/POST:ready", object);

    }

    public void matchBet() {
        String roomId;
        String spotId;
        JSONObject object = new JSONObject();
        try {
            assert user.getRoom() != null;
            roomId = user.getRoom().getName();
            spotId = user.getRoom().getSpot();
        } catch (NullPointerException e) {
            notifyOnPlayError.setValue(new Result.Error(e.getMessage()));
            return;
        }

        // TODO: We see the importance of persisting the DATA because using currentMinimumLocal is just an ugly shortcut
        try {
            object.put("room_id", roomId);
            object.put("spot_id", spotId);
            object.put("is_folding", false);
            object.put("raise", currentMinimumLocal);
        } catch(JSONException e) {
            notifyOnPlayError.setValue(new Result.Error(e.getMessage()));
            return;
        }
        dataSource.postRequest("room/POST:play", object);
    }

    public void fold() {
        String roomId;
        String spotId;
        JSONObject object = new JSONObject();
        try {
            assert user.getRoom() != null;
            roomId = user.getRoom().getName();
            spotId = user.getRoom().getSpot();
        } catch (NullPointerException e) {
            notifyOnPlayError.setValue(new Result.Error(e.getMessage()));
            return;
        }

        // TODO: We see the importance of persisting the DATA because using currentMinimumLocal is just an ugly shortcut
        try {
            object.put("room_id", roomId);
            object.put("spot_id", spotId);
            object.put("is_folding", true);
            object.put("raise", 0);
        } catch(JSONException e) {
            notifyOnPlayError.setValue(new Result.Error(e.getMessage()));
            return;
        }
        dataSource.postRequest("room/POST:play", object);
    }

    public void raise(@Nullable Integer raise) {
        String roomId;
        String spotId;
        JSONObject object = new JSONObject();

        if(raise == null) {
            notifyOnPlayError.setValue(new Result.Error("The raise must be a valid number!"));
            return;
        }

        try {
            assert user.getRoom() != null;
            roomId = user.getRoom().getName();
            spotId = user.getRoom().getSpot();
        } catch (NullPointerException e) {
            notifyOnPlayError.setValue(new Result.Error(e.getMessage()));
            return;
        }

        if(raise < currentMinimumLocal) {
            notifyOnPlayError.setValue(new Result.Error("You must bet at least $"+currentMinimumLocal+"!"));
            return;
        } else if(raise > user.getMoney()) {
            notifyOnPlayError.setValue(new Result.Error("This is more money than you currently have!"));
            return;
        }

        else if((raise % 10) != 0) {
            notifyOnPlayError.setValue(new Result.Error("The raise must be a multiple of 10!"));
            return;
        }

        // TODO: We see the importance of persisting the DATA because using currentMinimumLocal is just an ugly shortcut
        try {
            object.put("room_id", roomId);
            object.put("spot_id", spotId);
            object.put("is_folding", false);
            object.put("raise", raise);
        } catch(JSONException e) {
            notifyOnPlayError.setValue(new Result.Error(e.getMessage()));
            return;
        }
        dataSource.postRequest("room/POST:play", object);
    }


    private void processRoomState(RoomState data) {
        if(data.getError() == null) {
            currentMinimumLocal = data.getCurrentMinimum();
            currentMinimum.setValue(data.getCurrentMinimum());
            totalMoney.setValue(data.getTableTotal());
            if(!data.getWhoPlayed().equals(data.getMyIndex())) {
                Integer startingPlayerIndex = getLayoutForId(data.getMyIndex(), data.getWhoPlayed(), data.getnPlayers());
                ListManipulation.set(avatarType, startingPlayerIndex, (data.getActionType().equals(1))?User.FOLDED:User.NOT_FOLDED,false);
                ListManipulation.set(money, startingPlayerIndex, "$"+data.getPlayerNewMoney().toString(), false);
            } else {
                ListManipulation.set(avatarType, 0, (data.getActionType().equals(1))?User.FOLDED:User.NOT_FOLDED,false);
                isPlayerTurn.setValue(false);
                ListManipulation.set(money, 0, "$"+data.getPlayerNewMoney().toString(), false);
                user.setMoney(data.getPlayerNewMoney());
            }
            if(!data.getIsGameOver()) {
                if(data.getMyIndex() != data.getNextPlayer()) {
                    Integer startingPlayerIndex = getLayoutForId(data.getMyIndex(), data.getNextPlayer(), data.getnPlayers());
                    ListManipulation.set(avatarType, startingPlayerIndex, User.YOUR_TURN,false);
                } else {
                    ListManipulation.set(avatarType, 0, User.YOUR_TURN,false);
                    isPlayerTurn.setValue(true);
                }
            }
        }
        roomStateListener.setValue(data);
    }

    private void processRoomResults(RoomResults data) {
        if(data.getError() == null) {
            roomResults = data;

            Integer me = data.getMyIndex();
            Integer nplayers = data.getnPlayers();

            // It's nobody's turn for now
            for(int i = 0; i < nplayers; i++) {
                if(i == me) {
                    isPlayerTurn.setValue(false);
                    ListManipulation.set(avatarType, 0, User.NOT_FOLDED,false);
                    continue;
                }
                Integer index = getLayoutForId(me, i, nplayers);
                ListManipulation.set(avatarType, index, User.NOT_FOLDED,false);
            }
        }
        roomResultsListener.setValue(data);
    }

    public void updateRoomWithResults() {
        Integer me = roomResults.getMyIndex();
        Integer playing = roomResults.getCurrentPlayer();
        Integer nplayers = roomResults.getnPlayers();

        // Show the new player
        if(playing != me) {
            Integer index = getLayoutForId(me, playing, nplayers);
            ListManipulation.set(avatarType, index, User.YOUR_TURN,false);
        } else {
            ListManipulation.set(avatarType, 0, User.YOUR_TURN,false);
        }

        // Set my money
        ListManipulation.set(money, 0, "$"+roomResults.getPlayersMoney().get(me).toString(), false);
        user.setMoney(roomResults.getPlayersMoney().get(me));

        // Set everyone else's money
        for (int i = 0; i < nplayers; i++) {
            if(i == me) {
                ListManipulation.set(money, 0, "$"+roomResults.getPlayersMoney().get(me).toString(), false);
                continue;
            }
            Integer index = getLayoutForId(me, i, nplayers);
            ListManipulation.set(money, i, "$"+roomResults.getPlayersMoney().get(index).toString(), false);
        }

        if(roomResults.getCurrentPlayer() == me) {
            isPlayerTurn.setValue(true);
        }

        currentMinimumLocal = roomResults.getCurrentMinimum();
        currentMinimum.setValue(roomResults.getCurrentMinimum());
        totalMoney.setValue(150);

    }

    private void processInitialGameData(Result<InitialGameDataResult> data) {
        if(data instanceof Result.Success) {
            InitialGameDataResult res = ((Result.Success<InitialGameDataResult>) data).getData();

            if(res.getUserIndex() < 2) {
                Integer m = Integer.parseInt("10000")-50*(res.getUserIndex()+1);
                ListManipulation.set(money, 0, "$"+m.toString(), false);
            } else {
                ListManipulation.set(money, 0, "$"+res.getStartMoney().toString(), false);
            }
            user.setMoney(10000);
            for(int i = 0; i < res.getNumberOfPlayers(); i++) {
                if(res.getUserIndex() == i) continue;
                if(i < 2) {
                    Integer m = Integer.parseInt("10000")-50*(i+1);
                    Integer pIndex = getLayoutForId(res.getUserIndex(), i, res.getNumberOfPlayers());
                    ListManipulation.set(money, pIndex, "$"+m.toString(), false);
                } else {
                    Integer pIndex = getLayoutForId(res.getUserIndex(), i, res.getNumberOfPlayers());
                    ListManipulation.set(money, pIndex, "$"+"10000", false);
                }
            }
            ListManipulation.set(playerCards, 0, res.getCard1(),false);
            ListManipulation.set(playerCards, 1, res.getCard2(),false);
            ListManipulation.set(tableCards, 0, res.getTable1(),false);
            ListManipulation.set(tableCards, 1, res.getTable2(),false);
            ListManipulation.set(tableCards, 2, res.getTable3(),false);

            if(res.getUserIndex() != res.getCurrentPlayerIndex()) {
                Integer startingPlayerIndex = getLayoutForId(res.getUserIndex(), res.getCurrentPlayerIndex(), res.getNumberOfPlayers());
                ListManipulation.set(avatarType, startingPlayerIndex, User.YOUR_TURN,false);
            } else {
                ListManipulation.set(avatarType, 0, User.YOUR_TURN,false);
                isPlayerTurn.setValue(true);
            }
            currentMinimumLocal = res.getCurrentMinimum();
            currentMinimum.setValue(res.getCurrentMinimum());
            totalMoney.setValue(150);
            hasGameStarted.setValue(true);
            initialGameDataListener.setValue(((Result.Success<InitialGameDataResult>) data).getData());
        } else {
            initialGameDataListener.setValue(new InitialGameDataResult(((Result.Error)data).getError()));
        }

    }

    private void processReadyPlayerAuthorization(Result<Boolean> data) {
        if(data instanceof Result.Success) {
            if(((Result.Success<Boolean>) data).getData()) {
                ListManipulation.set(money, 0, "READY", false);
            }
        }
        readyPlayerAuthorizationListener.setValue(data);
    }

    private void processPreGamePlayerList(Result<TreeMap<String, User>> data) {
        if(hasGameStarted.getValue()) return;
        if(data instanceof Result.Error || data instanceof  Result.Progress) {
            preGamePlayerListListener.setValue(false);
            return;
        }
        TreeMap<String, User> fetchedUsers = ((Result.Success<TreeMap<String, User>>) data).getData();

        int size = fetchedUsers.size();
        int index = -1;
        int playerIndex = -1;

        if(size > 1) {
            for(TreeMap.Entry<String, User> entry : fetchedUsers.entrySet()) {
                playerIndex++;
                if (entry.getKey().equals(user.getRoom().getSpot())) {
                    break;
                }
            }

            for(TreeMap.Entry<String, User> entry : fetchedUsers.entrySet()) {
                index++;
                if (entry.getKey().equals(user.getRoom().getSpot())) {
                    continue;
                }
                Integer position = getLayoutForId(playerIndex, index, size);
                setDefaultLiveDataForUser(position, entry.getValue(), true);
            }
        }
        if(!hasUserInterfaceLoaded.getValue()) {
            hasUserInterfaceLoaded.setValue(true);
        }
        preGamePlayerListListener.setValue(true);
    }

    public void setDefaultLiveDataForUser(Integer index, User u, Boolean isRemote) {
        ListManipulation.set(avatar, index, u.getAvatar(), isRemote);
        ListManipulation.set(name, index, u.getName(), isRemote);
        ListManipulation.set(money, index, (u.getReady()?"READY":"NOT READY"), isRemote);
        ListManipulation.set(avatarType, index, User.NOT_FOLDED, isRemote);
    }

    private Integer getLayoutForId(int playerIndex, int id, int size) {
        if(size == 2) return 1;
        int newId = (id+(size-playerIndex))%size;
        if(newId == 1) {
            return 2;
        } else if(newId == 2) {
            return 1;
        } else {
            return 3;
        }
    }

    public LiveData<List<String>> getAvatarList() {
        return avatar;
    }

    public LiveData<List<String>> getAvatarTypeList() {
        return avatarType;
    }

    public LiveData<List<String>> getMoneyList() {
        return money;
    }

    public LiveData<List<String>> getNameList() {
        return name;
    }

    public LiveData<Boolean> getIsPlayerTurn() {
        return isPlayerTurn;
    }

    public LiveData<Boolean> getHasUserInterfaceLoaded() {
        return hasUserInterfaceLoaded;
    }

    public LiveData<Boolean> getHasGameStarted() {
        return hasGameStarted;
    }

    public LiveData<Integer> getTotalMoney() {
        return totalMoney;
    }

    public LiveData<Integer> getCurrentMinimum() {
        return currentMinimum;
    }

    public LiveData<List<Integer>> getTableCards() {
        return tableCards;
    }

    public LiveData<List<Integer>> getPlayerCards() {
        return playerCards;
    }

}
