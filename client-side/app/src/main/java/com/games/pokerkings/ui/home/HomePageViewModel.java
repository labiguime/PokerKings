package com.games.pokerkings.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.games.pokerkings.repositories.home.HomePageRepository;

public class HomePageViewModel extends ViewModel{

    private HomePageRepository homePageRepository;

    private MutableLiveData<String> avatar = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>("");

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
}
