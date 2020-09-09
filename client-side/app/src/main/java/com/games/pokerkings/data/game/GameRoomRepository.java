package com.games.pokerkings.data.game;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.R;
import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.models.Room;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.utils.*;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

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
        setDefaultLiveDataForUser(0, user);
        JSONObject object = new JSONObject();
        try {
            object.put("room_id", user.getRoom().getName());
        } catch(JSONException e) {

        }
        dataSource.postRequest("room/GET:preGamePlayerList", object);
        dataSource.getRequest("getPreGamePlayerList", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                onGetPlayers(data);
            }
        });
        hasUserInterfaceLoaded.setValue(false);
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

    private void onGetPlayers(JSONObject data) {
        try {
            HashMap<String, User> fetchedUsers = new HashMap<>();
            JSONArray array = data.getJSONArray("players");
            Log.d(TAG, "Ici");
            for(int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                fetchedUsers.put(obj.getString("_id"), new User(obj.getString("name"), obj.getString("avatar")));
            }

            TreeMap<String, User> sortUsers = new TreeMap<>(fetchedUsers);

            int size = sortUsers.size();
            int index = -1;
            int playerIndex = -1;

            if(size > 1) {
                for(TreeMap.Entry<String, User> entry : sortUsers.entrySet()) {

                    playerIndex++;
                    if (entry.getKey().equals(user.getRoom().getSpot())) {
                        break;
                    }
                }

                for(TreeMap.Entry<String, User> entry : sortUsers.entrySet()) {
                    Log.d(TAG, entry.getValue().getName());
                    index++;
                    if (entry.getKey().equals(user.getRoom().getSpot())) {
                        setDefaultLiveDataForUser(0, entry.getValue());
                        continue;
                    }
                    Integer position = getLayoutForId(playerIndex, index, size);
                    setDefaultLiveDataForUser(position, entry.getValue());
                }
            }

            /*if(!hasUserInterfaceLoaded.getValue()) {
                hasUserInterfaceLoaded.setValue(true);
            }*/

        } catch (JSONException e) {
            return;
        }
    }

    public void setDefaultLiveDataForUser(Integer index, User u) {
        Log.d("TAG", "name "+ u.getName() + " avatar "+u.getAvatar());
        ListManipulation.set(avatar, index, u.getAvatar());
        ListManipulation.set(name, index, u.getName());
        ListManipulation.set(money, index, "10000");
        ListManipulation.set(avatarType, index, User.NOT_FOLDED);
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
