package com.games.pokerkings.data.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.utils.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GameRoomRepository {

    private static volatile GameRoomRepository instance;
    private DataSource dataSource;
    private User user;
    private MutableLiveData<Boolean> hasUserInterfaceLoaded = new MutableLiveData<>(false);
    private MutableLiveData<List<String>> avatarType = new MutableLiveData<>();
    private MutableLiveData<List<String>> avatar = new MutableLiveData<>();
    private MutableLiveData<List<String>> name = new MutableLiveData<>();
    private MutableLiveData<List<String>> money = new MutableLiveData<>();

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

    public LiveData<Boolean> getHasUserInterfaceLoaded() {
        return hasUserInterfaceLoaded;
    }

    public void loadGamePageComponents(User user) {
        this.user = user;

        ListManipulation.set(avatar, 0, user.getAvatar());
        ListManipulation.set(name, 0, user.getName());
        ListManipulation.set(money, 0, user.getMoney().toString());
        ListManipulation.set(avatarType, 0, User.NOT_FOLDED);

        hasUserInterfaceLoaded.setValue(true);
    }

    public void alertPlayerReady() {
        String roomId;
        JSONObject object = new JSONObject();

        try {
            roomId = user.getRoom().getName();
        } catch (NullPointerException e) {
            return;
        }

        try {
            object.put("room_id", roomId);
            object.put("name", user.getName());
        } catch(JSONException e) {
            return;
        }

        dataSource.postRequest("room/POST:ready", object);
    }

}
