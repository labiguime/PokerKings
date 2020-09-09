package com.games.pokerkings.data.models;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String avatar;
    private Integer avatarId;
    private Integer money;
    private Boolean hasFolded;
    private Boolean ready;

    @Nullable
    private Room room;

    public User() {
        this.name = "";
        this.avatar = "avatar1";
        this.avatarId = 0;
        this.money = 10000;
        this.hasFolded = false;
        this.id = "";
        this.ready = false;
        this.room = null;

    }

    public User(String name, String avatar, String id, Boolean ready) {
        this.name = name;
        this.avatar = avatar;
        this.money = 10000;
        this.hasFolded = false;
        this.id = id;
        this.ready = ready;
        this.room = null;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
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

    public Integer getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
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

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
