package com.games.pokerkings.repositories;

import com.games.pokerkings.models.User;

public class HomePageDataSource {

    private User user;

    public HomePageDataSource() {
        this.user = new User();
    }

    public User getUser() {
        return this.user;
    }
}
