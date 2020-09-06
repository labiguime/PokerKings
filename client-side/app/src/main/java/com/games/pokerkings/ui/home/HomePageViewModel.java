package com.games.pokerkings.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.repositories.home.HomePageRepository;

public class HomePageViewModel extends ViewModel {

    private HomePageRepository homePageRepository;
    private MutableLiveData<String> avatar = new MutableLiveData<>();

    public HomePageViewModel(HomePageRepository repository) {
        this.homePageRepository = repository;
    }

    public LiveData<String> getAvatar() { return avatar; }
    public void changeAvatar() {
        Log.d("TEST", "Ok");
        avatar.setValue(homePageRepository.changeAvatar());
    }
}
