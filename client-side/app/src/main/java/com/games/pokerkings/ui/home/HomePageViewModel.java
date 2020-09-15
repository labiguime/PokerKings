package com.games.pokerkings.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.data.models.User;
import com.games.pokerkings.data.home.HomePageRepository;
import com.games.pokerkings.utils.Result;

public class HomePageViewModel extends ViewModel{

    private HomePageRepository homePageRepository;

    private MutableLiveData<String> avatar = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>("");
    private MutableLiveData<Boolean> hasPlayerPressedJoin;
    private MediatorLiveData<Result<User>> onJoinGame = new MediatorLiveData<>();
    private MutableLiveData<Result<User>> progressJoin = new MutableLiveData<>();

    public HomePageViewModel(HomePageRepository repository) {
        this.homePageRepository = repository;

        this.onJoinGame.addSource(homePageRepository.onReceiveJoinGameAuthorization(), value -> {
            onJoinGame.setValue(value);
        });

        this.onJoinGame.addSource(progressJoin, value -> {
            onJoinGame.setValue(value);
        });

        hasPlayerPressedJoin = new MutableLiveData<>(false);
    }

    public LiveData<String> getAvatar() { return avatar; }

    public MutableLiveData<String> getName() {
        return name;
    }

    public LiveData<Result<User>> getOnJoinGame() {
        return onJoinGame;
    }

    public LiveData<Boolean> getHasPlayerPressedJoin() {
        return hasPlayerPressedJoin;
    }

    public void setHasPlayerPressedJoin() {
        hasPlayerPressedJoin.setValue(!hasPlayerPressedJoin.getValue());
    }

    public void setUserHasJoinedRoom() {
        progressJoin.setValue(new Result.Progress(true));
    }

    public void setName(String s) {
        homePageRepository.setUsername(s);
    }

    public void setAvatar() {
        avatar.setValue(homePageRepository.changeAvatar());
    }

    public void onJoinGameButtonClicked() {
        setHasPlayerPressedJoin();
        homePageRepository.joinGame();
    }

}
