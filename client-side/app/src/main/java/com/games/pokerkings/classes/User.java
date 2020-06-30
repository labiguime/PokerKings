package com.games.pokerkings.classes;

public class User {
    private String id;
    private String roomId;
    private String spotId;
    private String name;
    private String avatar;
    private Integer money;
    private Boolean hasFolded;
    private Boolean ready;

    public User() {
        this.name = "Unknown";
        this.avatar = "avatar1";
        this.money = 10000;
        this.hasFolded = false;
        this.id = "";
        this.roomId = "";
        this.ready = false;
    }

    public User(String name, String avatar, String id, String roomId, String spotId, Boolean ready) {
        this.name = name;
        this.avatar = avatar;
        this.money = 10000;
        this.hasFolded = false;
        this.id = id;
        this.roomId = roomId;
        this.spotId = spotId;
        this.ready = ready;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nickname) {
        this.name = nickname;
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

}
