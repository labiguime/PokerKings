package com.games.pokerkings.classes;

public class User {

    private String nickname;
    private String avatar;
    private Integer money;
    private Boolean hasFolded;
    private Integer tableId;

    public User() {
        this.nickname = "Unknown";
        this.avatar = "avatar1";
        this.money = 10000;
        this.hasFolded = false;
        this.tableId = -1;
    }

    public User(String nickname, String avatar) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.money = 10000;
        this.hasFolded = false;
        this.tableId = -1;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
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

    public Boolean getHasFolded() {
        return hasFolded;
    }

    public void setHasFolded(Boolean hasFolded) {
        this.hasFolded = hasFolded;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
}
