package com.games.pokerkings.data.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.utils.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TreeMap;

public class GameRoomRepository {

    private static volatile GameRoomRepository instance;
    private DataSource dataSource;
    private User user;
    private MutableLiveData<Boolean> hasUserInterfaceLoaded = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> hasGameStarted = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isPlayerReady = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isPlayerTurn = new MutableLiveData<>(false);
    private MutableLiveData<List<String>> avatarType = new MutableLiveData<>();
    private MutableLiveData<List<String>> avatar = new MutableLiveData<>();
    private MutableLiveData<List<String>> name = new MutableLiveData<>();
    private MutableLiveData<List<String>> money = new MutableLiveData<>();

    private MediatorLiveData<Boolean> preGamePlayerListListener = new MediatorLiveData<>();

    public static final String TAG = "LOG_GAME_ROOM";

    public GameRoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.user = new User();

        for(int i = 0; i < 4; i++) {
            ListManipulation.append(avatarType, "");
            ListManipulation.append(avatar, "");
            ListManipulation.append(name, "");
            ListManipulation.append(money, "");
        }

        preGamePlayerListListener.addSource(dataSource.onReceivePreGamePlayerList(), this::processPreGamePlayerList);
    }

    public LiveData<Boolean> onReceivePreGamePlayerList() {
        return preGamePlayerListListener;
    }

    public static GameRoomRepository getInstance() {
        if (instance == null) {
            instance = new GameRoomRepository(new DataSource());
        }
        return instance;
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

    public LiveData<Boolean> getIsPlayerReady() {
        return isPlayerReady;
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
        /*isPlayerReady.setValue(true);
        String roomId;
        JSONObject object = new JSONObject();
        try {
            roomId = user.getRoom().getName();
        } catch (NullPointerException e) {
            return;
        }

        // TODO: This request should take user id or spot id instead of name
        try {
            object.put("room_id", roomId);
            object.put("name", user.getName());
        } catch(JSONException e) {
            return;
        }

        dataSource.postRequest("room/POST:ready", object);
        ListManipulation.set(money, 0, "READY", false);*/

    }

    private void processPreGamePlayerList(Result<TreeMap<String, User>> data) {
        if(data instanceof Result.Error || data instanceof  Result.Progress) {
            preGamePlayerListListener.setValue(false);
            return;
        }
        TreeMap<String, User> fetchedUsers = (TreeMap<String, User>) ((Result.Success) data).getData();

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

}
