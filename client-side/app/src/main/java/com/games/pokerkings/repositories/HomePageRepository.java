package com.games.pokerkings.repositories;

import android.util.Log;

import com.games.pokerkings.models.User;

public class HomePageRepository {

    private static volatile HomePageRepository instance;
    private HomePageDataSource dataSource;

    public HomePageRepository(HomePageDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static HomePageRepository getInstance(HomePageDataSource dataSource) {
        if (instance == null) {
            instance = new HomePageRepository(dataSource);
        }
        return instance;
    }

    public String changeAvatar() {

        User user = dataSource.getUser();
        Integer avatar = user.getAvatarId();

        avatar = ((avatar+1)%6);
        String avatarFileName = "avatar" + (avatar+1);

        user.setAvatarId(avatar);
        user.setAvatar(avatarFileName);

        return avatarFileName;
    }
}
