package com.games.pokerkings.repositories;

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
        return "";
    }
}
