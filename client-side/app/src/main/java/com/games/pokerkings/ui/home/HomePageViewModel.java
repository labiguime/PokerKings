package com.games.pokerkings.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.repositories.HomePageRepository;

public class HomePageViewModel extends ViewModel {

    private MutableLiveData<String> avatar;

    public HomePageViewModel() { }

    LiveData<String> getAvatar() { return avatar; }

    public void changeAvatar() {
        avatar.setValue(HomePageRepository.changeAvatar());
    }
}
