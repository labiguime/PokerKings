package com.games.pokerkings.data.home;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.models.*;
import com.games.pokerkings.utils.Constants;
import com.games.pokerkings.utils.Result;

import org.json.JSONException;
import org.json.JSONObject;

public class HomePageRepository {

    private static volatile HomePageRepository instance;

    private DataSource dataSource;
    private User user;

    private MediatorLiveData<Result<User>> joinGameAuthorizationListener = new MediatorLiveData<>();
    private MutableLiveData<Result.Error> notifyJoinGameError = new MutableLiveData<>();

    public HomePageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.user = new User();

        this.joinGameAuthorizationListener.addSource(dataSource.onReceiveJoinGameAuthorization(), this::processJoinGameAuthorization);
        this.joinGameAuthorizationListener.addSource(notifyJoinGameError, value -> joinGameAuthorizationListener.setValue(value));
    }

    public void processJoinGameAuthorization(Result<Room> data) {
        if(data instanceof Result.Error) {
            joinGameAuthorizationListener.setValue(new Result.Error(((Result.Error) data).getError()));
        }
        else if(data instanceof  Result.Success){
            Room room = (Room) ((Result.Success) data).getData();
            user.setRoom(room);
            joinGameAuthorizationListener.setValue(new Result.Success<User>(user));
        } else {
            joinGameAuthorizationListener.setValue(new Result.Progress(true));
        }
    }

    public LiveData<Result<User>> onReceiveJoinGameAuthorization() {
        return joinGameAuthorizationListener;
    }

    public static HomePageRepository getInstance(DataSource dataSource) {
        if (instance == null) {
            instance = new HomePageRepository(dataSource);
        }
        return instance;
    }

    public void leaveGameRoom() {
        JSONObject leaveObject = new JSONObject();
        dataSource.postRequest("room/POST:leave", leaveObject);
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
        /* Data validation */
        @Nullable
        String usernameCheckResult = isUsernameValid(user);
        if(usernameCheckResult != null) {
            notifyJoinGameError.setValue(new Result.Error(usernameCheckResult));
            return;
        }

        /* Construct an object that we will post to socket.io */
        JSONObject joinObject = new JSONObject();
        try {
            joinObject.put("room", "Room#1");
            joinObject.put("name", user.getName());
            joinObject.put("avatar", avatarToString(user.getAvatarId()));
        } catch( JSONException e ) {
            notifyJoinGameError.setValue(new Result.Error(e.getMessage()));
            return;
        }
        dataSource.postRequest("room/POST:join", joinObject);
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
