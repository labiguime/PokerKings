package com.games.pokerkings.ui.home;

import android.graphics.Paint;

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
    private MutableLiveData<JoinGameResult> joinGameResult = new MutableLiveData<>();

    public HomePageViewModel(HomePageRepository repository) {
        this.homePageRepository = repository;
    }

    public LiveData<String> getAvatar() { return avatar; }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(String s) {
        homePageRepository.setUsername(s);
    }

    public void setAvatar() {
        avatar.setValue(homePageRepository.changeAvatar());
    }

    public void joinGame() {
        @Nullable
        Integer usernameCheckResult = isUsernameValid();
        if(usernameCheckResult != null) {
            joinGameResult.setValue(new JoinGameResult(usernameCheckResult));
        } else {
            Result<User> queryResult = homePageRepository.joinGame();
            if(queryResult instanceof Result.Success) {
                User data = ((Result.Success<User>) queryResult).getData();
                joinGameResult.setValue(new JoinGameResult(true, data));
            } else {
                Integer error = ((Result.Error) queryResult).getError();
                joinGameResult.setValue(new JoinGameResult(error));
            }
        }

    }

    @Nullable
    public Integer isUsernameValid() {
        String nameToCheck = name.getValue();
        if(nameToCheck.length() < 1) {
            return R.string.error_name_too_short;
        } else if(nameToCheck.length() > 15) {
            return R.string.error_name_too_long;
        } else {
            return null;
        }
    }
}
