package com.games.pokerkings.ui.game;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.game.GameRoomRepository;
import com.games.pokerkings.data.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GameRoomViewModel extends ViewModel {

    private GameRoomRepository gameRoomRepository;
    private MutableLiveData<Boolean> isReadyButtonVisible = new MutableLiveData<>(true);
    private LiveData<Boolean> hasUserInterfaceLoaded;
    private LiveData<Boolean> hasGameStarted;
    private LiveData<List<String>> avatarType;
    private LiveData<List<String>> avatar;
    private LiveData<List<String>> name;
    private LiveData<List<String>> money;

    public GameRoomViewModel() {
        this.gameRoomRepository = GameRoomRepository.getInstance();
        this.hasUserInterfaceLoaded = gameRoomRepository.getHasUserInterfaceLoaded();
        this.hasGameStarted = gameRoomRepository.getHasGameStarted();
        this.avatarType = gameRoomRepository.getAvatarTypeList();
        this.avatar = gameRoomRepository.getAvatarList();
        this.name = gameRoomRepository.getNameList();
        this.money = gameRoomRepository.getMoneyList();
    }

    public LiveData<Boolean> getIsReadyButtonVisible() {
        return isReadyButtonVisible;
    }

    public LiveData<Boolean> getHasUserInterfaceLoaded() {
        return hasUserInterfaceLoaded;
    }

    public LiveData<Boolean> getHasGameStarted() {
        return hasGameStarted;
    }

    public LiveData<List<String>> getAvatar() {
        return avatar;
    }

    public LiveData<List<String>> getAvatarType() {
        return avatarType;
    }

    public LiveData<List<String>> getMoney() {
        return money;
    }

    public LiveData<List<String>> getName() {
        return name;
    }

    public void setUserInterfaceForUser(User u) {
        gameRoomRepository.loadGamePageComponents(u);
    }

    public void onReadyButtonClicked() {
        isReadyButtonVisible.setValue(false);
        gameRoomRepository.alertPlayerReady();
    }
}
