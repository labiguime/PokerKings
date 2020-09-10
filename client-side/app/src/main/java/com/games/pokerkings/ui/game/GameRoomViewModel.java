package com.games.pokerkings.ui.game;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.data.game.GameRoomRepository;
import com.games.pokerkings.data.models.User;

import java.util.List;

public class GameRoomViewModel extends ViewModel {

    private GameRoomRepository gameRoomRepository;
    private LiveData<Boolean> receivePreGamePlayerList;
    private LiveData<Boolean> hasUserInterfaceLoaded;
    private LiveData<Boolean> hasGameStarted;
    private LiveData<Boolean> isPlayerReady;
    private LiveData<Boolean> isPlayerTurn;
    private LiveData<List<String>> avatarType;
    private LiveData<List<String>> avatar;
    private LiveData<List<String>> name;
    private LiveData<List<String>> money;

    public GameRoomViewModel() {
        this.gameRoomRepository = GameRoomRepository.getInstance();
        this.hasUserInterfaceLoaded = gameRoomRepository.getHasUserInterfaceLoaded();
        this.receivePreGamePlayerList = gameRoomRepository.onReceivePreGamePlayerList();
        this.hasGameStarted = gameRoomRepository.getHasGameStarted();
        this.isPlayerReady = gameRoomRepository.getIsPlayerReady();
        this.isPlayerTurn = gameRoomRepository.getIsPlayerTurn();

        this.avatarType = gameRoomRepository.getAvatarTypeList();
        this.avatar = gameRoomRepository.getAvatarList();
        this.name = gameRoomRepository.getNameList();
        this.money = gameRoomRepository.getMoneyList();
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

    public LiveData<Boolean> onReceivePreGamePlayerList() {
        return receivePreGamePlayerList;
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
        gameRoomRepository.alertPlayerReady();
    }
}
