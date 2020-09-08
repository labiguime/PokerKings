package com.games.pokerkings.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.R;
import com.games.pokerkings.models.User;
import com.games.pokerkings.repositories.home.HomePageRepository;
import com.games.pokerkings.utils.Result;

public class HomePageViewModel extends ViewModel{

    private HomePageRepository homePageRepository;

    private MutableLiveData<String> avatar = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>("");
    private MutableLiveData<Boolean> hasPlayerPressedJoin = new MutableLiveData<>(false);

    private LiveData<Result<User>> onJoinGame;

    public HomePageViewModel(HomePageRepository repository) {
        this.homePageRepository = repository;
        this.onJoinGame = homePageRepository.getJoinGame();
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
