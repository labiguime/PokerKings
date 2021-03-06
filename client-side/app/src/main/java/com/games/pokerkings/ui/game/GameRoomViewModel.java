package com.games.pokerkings.ui.game;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.data.DisconnectionType;
import com.games.pokerkings.data.InitialGameDataResult;
import com.games.pokerkings.data.RoomResults;
import com.games.pokerkings.data.RoomState;
import com.games.pokerkings.data.game.GameRoomRepository;
import com.games.pokerkings.data.models.User;
import com.games.pokerkings.utils.Result;

import java.util.List;

public class GameRoomViewModel extends ViewModel {

    private GameRoomRepository gameRoomRepository;
    private MutableLiveData<Boolean> isPlayerReady;
    private MutableLiveData<Boolean> isReadyButtonVisible;
    private MutableLiveData<Boolean> hasPressedAButton;
    private MutableLiveData<String> raiseAmount;
    private LiveData<Boolean> hasUserInterfaceLoaded;
    private LiveData<Boolean> hasGameStarted;
    private LiveData<Boolean> isPlayerTurn;
    private LiveData<List<String>> avatarType;
    private LiveData<List<String>> avatar;
    private LiveData<List<String>> name;
    private LiveData<List<String>> money;
    private LiveData<Integer> totalMoney;
    private LiveData<Integer> currentMinimum;
    private LiveData<Boolean> receivePreGamePlayerList;
    private LiveData<Result<Boolean>> receiveReadyPlayerAuthorization;
    private LiveData<Result<Boolean>> receiveAuthorizationToPlay;
    private LiveData<DisconnectionType> receiveDisconnectEvent;
    private LiveData<InitialGameDataResult> receiveInitialGameData;
    private LiveData<RoomResults> receiveRoomResults;
    private LiveData<RoomState> receiveRoomState;

    public GameRoomViewModel() {
        this.gameRoomRepository = GameRoomRepository.getInstance();
        isPlayerReady = new MutableLiveData<>(false);
        isReadyButtonVisible = new MutableLiveData<>(true);
        hasPressedAButton = new MutableLiveData<>(false);
        raiseAmount = new MutableLiveData<>("0");

        this.receiveReadyPlayerAuthorization = Transformations.map(gameRoomRepository.onReceiveReadyPlayerAuthorization(), value -> {
            if(value instanceof Result.Error) {
                isReadyButtonVisible.setValue(true);
                isPlayerReady.setValue(false);
            } else {
                isReadyButtonVisible.setValue(false);
                isPlayerReady.setValue(true);
            }
            return value;
        });

        this.receiveDisconnectEvent = Transformations.map(gameRoomRepository.onReceiveDisconnectEvent(), value -> {
            isReadyButtonVisible.setValue(true);
            isPlayerReady.setValue(false);
            return value;
        });

        this.receiveRoomResults = gameRoomRepository.onReceiveRoomResults();
        this.receivePreGamePlayerList = gameRoomRepository.onReceivePreGamePlayerList();
        this.receiveInitialGameData = gameRoomRepository.onReceiveInitialGameData();
        this.hasUserInterfaceLoaded = gameRoomRepository.getHasUserInterfaceLoaded();
        this.receiveAuthorizationToPlay = Transformations.map(gameRoomRepository.onReceiveAuthorizationToPlay(), value -> {
            this.hasPressedAButton.setValue(false);
            return value;
        });
        this.receiveRoomState = gameRoomRepository.onReceiveRoomState();
        this.totalMoney = gameRoomRepository.getTotalMoney();
        this.currentMinimum = gameRoomRepository.getCurrentMinimum();
        this.hasGameStarted = gameRoomRepository.getHasGameStarted();
        this.isPlayerTurn = gameRoomRepository.getIsPlayerTurn();
        this.avatarType = gameRoomRepository.getAvatarTypeList();
        this.avatar = gameRoomRepository.getAvatarList();
        this.name = gameRoomRepository.getNameList();
        this.money = gameRoomRepository.getMoneyList();
    }

    public LiveData<Integer> getTotalMoney() {
        return totalMoney;
    }

    public LiveData<Integer> getCurrentMinimum() {
        return currentMinimum;
    }

    public LiveData<Boolean> getIsPlayerReady() {
        return isPlayerReady;
    }

    public LiveData<Boolean> getIsReadyButtonVisible() {
        return isReadyButtonVisible;
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

    public LiveData<Result<Boolean>> onReceiveAuthorizationToPlay() {
        return receiveAuthorizationToPlay;
    }

    public LiveData<Result<Boolean>> onReceiveReadyPlayerAuthorization() {
        return receiveReadyPlayerAuthorization;
    }

    public LiveData<InitialGameDataResult> onReceiveInitialGameData() {
        return receiveInitialGameData;
    }

    public LiveData<DisconnectionType> onReceiveDisconnectEvent() {
        return receiveDisconnectEvent;
    }

    public LiveData<RoomResults> onReceiveRoomResults() {
        return receiveRoomResults;
    }

    public LiveData<RoomState> onReceiveRoomState() {
        return receiveRoomState;
    }

    public LiveData<Boolean> getHasPressedAButton() {
        return hasPressedAButton;
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

    public MutableLiveData<String> getRaiseAmount() {
        return raiseAmount;
    }

    public void setRaiseAmount(String amount) {
        raiseAmount.setValue(amount);
    }

    public void triggerAfterRoomResultsChanges() {
        gameRoomRepository.updateRoomWithResults();
    }

    public void setUserInterfaceForUser(User u) {
        gameRoomRepository.loadGamePageComponents(u);
    }

    public void onReadyButtonClicked() {
        @Nullable
        Boolean isReadyVisible = isReadyButtonVisible.getValue();
        if(isReadyVisible != null) {
            if (isReadyVisible) {
                isReadyButtonVisible.setValue(false);
                gameRoomRepository.alertPlayerReady();
            }
        }
    }

    public void onRaiseButtonClicked() {
        @Nullable
        Boolean hasPressedAButtonValue = hasPressedAButton.getValue();

        String value = raiseAmount.getValue();
        if(value == null) return;

        @Nullable
        Integer raise = Integer.parseInt(value);

        if(hasPressedAButtonValue != null) {
            if (!hasPressedAButtonValue) {
                hasPressedAButton.setValue(true);
                gameRoomRepository.raise(raise);
            }
        }
    }

    public void onMatchButtonClicked() {
        @Nullable
        Boolean hasPressedAButtonValue = hasPressedAButton.getValue();
        if(hasPressedAButtonValue != null) {
            if (!hasPressedAButtonValue) {
                hasPressedAButton.setValue(true);
                gameRoomRepository.matchBet();
            }
        }
    }

    public void onFoldButtonClicked() {
        @Nullable
        Boolean hasPressedAButtonValue = hasPressedAButton.getValue();
        if(hasPressedAButtonValue != null) {
            if (!hasPressedAButtonValue) {
                hasPressedAButton.setValue(true);
                gameRoomRepository.fold();
            }
        }
    }

    public void onIncreaseButtonClicked() {
        @Nullable
        Boolean hasPressedAButtonValue = hasPressedAButton.getValue();
        if(hasPressedAButtonValue != null) {
            if (!hasPressedAButtonValue) {
                @Nullable
                String value = raiseAmount.getValue();
                if (value == null) return;

                @Nullable
                int raise = Integer.parseInt(value);

                if (raise < 50) {
                    raiseAmount.setValue("50");
                } else if (raise < 100) {
                    raiseAmount.setValue("100");
                } else if (raise < 500) {
                    Integer newRaise = raise+100;
                    raiseAmount.setValue(newRaise.toString());
                } else {
                    Integer newRaise = raise+500;
                    raiseAmount.setValue(newRaise.toString());
                }
            }
        }
    }

    public void onDecreaseButtonClicked() {
        @Nullable
        Boolean hasPressedAButtonValue = hasPressedAButton.getValue();
        if(hasPressedAButtonValue != null) {
            if (!hasPressedAButtonValue) {
                @Nullable
                String value = raiseAmount.getValue();
                if (value == null) return;

                @Nullable
                int raise = Integer.parseInt(value);

                if (raise > 500) {
                    Integer newRaise = raise-500;
                    raiseAmount.setValue(newRaise.toString());
                } else if (raise > 100) {
                    Integer newRaise = raise-100;
                    raiseAmount.setValue(newRaise.toString());
                } else if (raise > 50) {
                    Integer newRaise = raise-50;
                    raiseAmount.setValue(newRaise.toString());
                } else {
                    raiseAmount.setValue("50");
                }
            }
        }
    }
}
