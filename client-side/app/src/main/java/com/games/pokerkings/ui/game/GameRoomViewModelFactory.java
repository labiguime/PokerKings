package com.games.pokerkings.ui.game;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.games.pokerkings.data.DataSource;
import com.games.pokerkings.data.game.GameRoomRepository;
import com.games.pokerkings.data.home.HomePageRepository;

public class GameRoomViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameRoomViewModelFactory.class)) {
            return (T) new GameRoomViewModel(GameRoomRepository.getInstance(new DataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
