package com.games.pokerkings.ui.game;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.data.game.GameRoomRepository;
import com.games.pokerkings.data.models.User;

public class GameRoomViewModel extends ViewModel {

    private GameRoomRepository gameRoomRepository;
    private MutableLiveData<Boolean> isReadyButtonVisible = new MutableLiveData<>(true);
    private LiveData<Boolean> hasUserInterfaceLoaded = new MutableLiveData<Boolean>(false);

    public GameRoomViewModel(GameRoomRepository gameRoomRepository) {
        this.gameRoomRepository = gameRoomRepository;
        this.hasUserInterfaceLoaded = gameRoomRepository.getHasUserInterfaceLoaded();
    }

    public LiveData<Boolean> getIsReadyButtonVisible() {
        return isReadyButtonVisible;
    }

    public void setUserInterfaceForUser(User u) {
        gameRoomRepository.loadGamePageComponents(u);
    }

    public void onReadyButtonClicked() {
        isReadyButtonVisible.setValue(false);
        gameRoomRepository.alertPlayerReady();
    }
}
