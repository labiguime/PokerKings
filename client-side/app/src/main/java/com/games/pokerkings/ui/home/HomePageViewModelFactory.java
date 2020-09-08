package com.games.pokerkings.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.games.pokerkings.repositories.home.HomePageDataSource;
import com.games.pokerkings.repositories.home.HomePageRepository;

public class HomePageViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomePageViewModel.class)) {
            return (T) new HomePageViewModel(HomePageRepository.getInstance(new HomePageDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
