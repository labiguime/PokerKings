package com.games.pokerkings.data.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.data.DataSource;
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
    private MutableLiveData<Boolean> hasUserInterfaceLoaded = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> hasGameStarted = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isPlayerTurn = new MutableLiveData<>(false);
    private MutableLiveData<List<String>> avatarType = new MutableLiveData<>(Arrays.asList("", "", "", ""));
    private MutableLiveData<List<String>> avatar = new MutableLiveData<>(Arrays.asList("", "", "", ""));
    private MutableLiveData<List<String>> name = new MutableLiveData<>(Arrays.asList("", "", "", ""));
    private MutableLiveData<List<String>> money = new MutableLiveData<>(Arrays.asList("", "", "", ""));
    private MutableLiveData<Result.Error> notifyReadyPlayerError = new MutableLiveData<>();
    private MediatorLiveData<Result<Boolean>> readyPlayerAuthorizationListener = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> preGamePlayerListListener = new MediatorLiveData<>();
    private MediatorLiveData<InitialGameDataResult> initialGameDataListener = new MediatorLiveData<>();
    private MediatorLiveData<RoomResults> roomResultsListener = new MediatorLiveData<>();
    private MediatorLiveData<RoomState> roomStateListener = new MediatorLiveData<>();
    private LiveData<Result<Boolean>> authorizationToPlayListener;
    private MutableLiveData<Integer> totalMoney = new MutableLiveData<>();
    private MutableLiveData<Integer> currentMinimum = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> tableCards = new MutableLiveData<>(Arrays.asList(-1, -1, -1, -1, -1));
    private MutableLiveData<List<Integer>> playerCards = new MutableLiveData<>(Arrays.asList(-1, -1));
    private Integer currentMinimumLocal;

    public static final String TAG = "LOG_GAME_ROOM";

    public GameRoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.user = new User();
        this.roomResults = new RoomResults("No results");
        currentMinimumLocal = 0;
        this.preGamePlayerListListener.addSource(dataSource.onReceivePreGamePlayerList(), this::processPreGamePlayerList);
        this.readyPlayerAuthorizationListener.addSource(dataSource.onReceiveReadyPlayerAuthorization(), this::processReadyPlayerAuthorization);
        this.initialGameDataListener.addSource(dataSource.onReceiveInitialRoomData(), this::processInitialGameData);
        this.roomStateListener.addSource(dataSource.onReceiveRoomState(), this::processRoomState);
        this.roomResultsListener.addSource(dataSource.onReceiveRoomResults(), this::processRoomResults);
        this.readyPlayerAuthorizationListener.addSource(notifyReadyPlayerError, value -> readyPlayerAuthorizationListener.setValue(value));
        this.authorizationToPlayListener = dataSource.onReceiveAuthorizationToPlay();
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

    public static GameRoomRepository getInstance() {
        if (instance == null) {
            instance = new GameRoomRepository(new DataSource());
        }
        return instance;
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
            //notifyReadyPlayerError.setValue(new Result.Error(e.getMessage()));
            return;
        }

        // TODO: We see the importance of persisting the DATA because using currentMinimumLocal is just an ugly shortcut
        try {
            object.put("room_id", roomId);
            object.put("spot_id", spotId);
            object.put("is_folding", false);
            object.put("raise", currentMinimumLocal);
        } catch(JSONException e) {
            //notifyReadyPlayerError.setValue(new Result.Error(e.getMessage()));
            return;
        }
        dataSource.postRequest("room/POST:play", object);
        return;
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
            //notifyReadyPlayerError.setValue(new Result.Error(e.getMessage()));
            return;
        }

        // TODO: We see the importance of persisting the DATA because using currentMinimumLocal is just an ugly shortcut
        try {
            object.put("room_id", roomId);
            object.put("spot_id", spotId);
            object.put("is_folding", true);
            object.put("raise", 0);
        } catch(JSONException e) {
            //notifyReadyPlayerError.setValue(new Result.Error(e.getMessage()));
            return;
        }
        dataSource.postRequest("room/POST:play", object);
        return;
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
            isPlayerTurn.setValue(false);
            ListManipulation.set(avatarType, 0, User.NOT_FOLDED,false);
            for(int i = 1; i < nplayers; i++) {
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
        Integer index = getLayoutForId(me, playing, nplayers);
        ListManipulation.set(avatarType, index, User.YOUR_TURN,false);

        // Set my money
        ListManipulation.set(money, 0, "$"+roomResults.getPlayersMoney().get(me).toString(), false);

        // Set everyone else's money
        for (int i = 1; i < nplayers; i++) {
            index = getLayoutForId(me, i, nplayers);
            ListManipulation.set(money, i, "$"+roomResults.getPlayersMoney().get(index).toString(), false);
        }

        if(roomResults.getCurrentPlayer() == me) {
            isPlayerTurn.setValue(true);
        }

        currentMinimumLocal = roomResults.getCurrentMinimum();
        currentMinimum.setValue(roomResults.getCurrentMinimum());
        totalMoney.setValue(0);

    }

    private void processInitialGameData(Result<InitialGameDataResult> data) {
        if(data instanceof Result.Success) {
            InitialGameDataResult res = ((Result.Success<InitialGameDataResult>) data).getData();
            for(int i = 0; i < 4; i++) {
                ListManipulation.set(money, i, "$"+res.getStartMoney().toString(), false);
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
            totalMoney.setValue(0);
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
