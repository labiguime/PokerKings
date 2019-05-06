package com.games.pokerkings.classes;

public class User {

    private String nickname;
    private int avatar;
    private int money;
    private Boolean hasFolded;

    public User() {
        this.nickname = "Unknwon";
        this.avatar = 0;
        this.money = 0;
        this.hasFolded = false;
    }

    public User(String nickname, int avatar) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.money = 10000;
        this.hasFolded = false;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAvatar() {
        return avatar;
    }

    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public boolean isHasFolded() {
        return hasFolded;
    }

    public void setHasFolded(boolean hasFolded) {
        this.hasFolded = hasFolded;
    }
}
