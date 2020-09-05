package com.games.pokerkings.repositories;

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
        Integer avatar = user.getAvatar();
        avatar = ((avatar+1)%6);
        user.setAvatar(avatar);
        String avatarFileName = "avatar" + (avatar+1);
        return avatarFileName;
    }
}
