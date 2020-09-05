package com.games.pokerkings.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomePageViewModel extends ViewModel {

    private MutableLiveData<Integer> avatar;
    HomePageViewModel() { }
    LiveData<Integer> getAvatar() { return avatar; }
}
