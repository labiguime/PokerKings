package com.games.pokerkings.data.home;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.R;
import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.models.*;
import com.games.pokerkings.utils.Constants;
import com.games.pokerkings.utils.Result;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

public class HomePageRepository {


    private static volatile HomePageRepository instance;
    private DataSource dataSource;
    private MutableLiveData<Result<User>> joinGame = new MutableLiveData<>();
    private User user;

    public HomePageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.user = new User();
    }

    public static HomePageRepository getInstance(DataSource dataSource) {
        if (instance == null) {
            instance = new HomePageRepository(dataSource);
        }
        return instance;
    }

    public void setUsername(String name) {
        user.setName(name);
    }

    public String changeAvatar() {
        Integer avatar = user.getAvatarId();
        avatar = incrementAvatar(avatar);

        String avatarFileName = avatarToString(avatar);

        user.setAvatarId(avatar);
        user.setAvatar(avatarFileName);

        return avatarFileName;
    }

    public void joinGame() {

        /* Construct an object that we will post to socket.io */
        JSONObject joinObject = new JSONObject();
        @Nullable
        String usernameCheckResult = isUsernameValid(user);
        if(usernameCheckResult != null) {
            joinGame.setValue(new Result.Error(usernameCheckResult));
            return;
        }

        try {
            joinObject.put("room", "Room#1");
            joinObject.put("name", user.getName());
            joinObject.put("avatar", avatarToString(user.getAvatarId()));
        } catch( JSONException e ) {
            joinGame.setValue(new Result.Error(null));
            return;
        }

        dataSource.postRequest("room/POST:join", joinObject);
        dataSource.getRequest("joinRoom", args -> {
            JSONObject data = (JSONObject) args[0];
            onJoinGame(data);
        });

        joinGame.setValue(new Result.Progress(true));
    }

    public LiveData<Result<User>> getJoinGame() {
        return joinGame;
    }

    public void onJoinGame(JSONObject data) {
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
                joinGame.postValue(new Result.Error(message));
            } else {
                user.setRoom(new Room(room, spot));
                joinGame.postValue(new Result.Success<User>(user));
            }

        } catch (JSONException e) {
            //e.getMessage()
            joinGame.postValue(new Result.Error(e.getMessage()));
            return;
        }
    }

    @Nullable
    public String isUsernameValid(User u) {
        String nameToCheck = u.getName();
        if(nameToCheck.length() < 1) {
            return Constants.ERROR_NAME_TOO_SHORT;
        } else if(nameToCheck.length() > 15) {
            return Constants.ERROR_NAME_TOO_LONG;
        } else {
            return null;
        }
    }

    private String avatarToString(Integer avatar) {
        return ("avatar" + (avatar+1));
    }

    private Integer incrementAvatar(Integer avatar) {
        return ((avatar+1)%6);
    }
}
