package com.games.pokerkings.data.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class GameRoomRepository {

    private static volatile GameRoomRepository instance;
    private DataSource dataSource;
    private User user;
    private MutableLiveData<Boolean> hasUserInterfaceLoaded = new MutableLiveData<>(false);

    public GameRoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.user = new User();
    }

    public static GameRoomRepository getInstance(DataSource dataSource) {
        if (instance == null) {
            instance = new GameRoomRepository(dataSource);
        }
        return instance;
    }

    public LiveData<Boolean> getHasUserInterfaceLoaded() {
        return hasUserInterfaceLoaded;
    }

    public void loadGamePageComponents(User user) {
        this.user = user;
        hasUserInterfaceLoaded.setValue(true);
    }

    public void alertPlayerReady() {
        JSONObject object = new JSONObject();
        try {
            object.put("room_id", user.getRoom().getName());
            object.put("name", user.getName());
        } catch(JSONException e) {
        }
        dataSource.postRequest("room/POST:ready", object);
    }
}
