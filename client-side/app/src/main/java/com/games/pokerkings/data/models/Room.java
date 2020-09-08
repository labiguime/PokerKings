package com.games.pokerkings.data.models;

import java.io.Serializable;

public class Room implements Serializable {

    String spot;
    String name;

    public Room(String name, String spot) {
        this.name = name;
        this.spot = spot;
    }

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
