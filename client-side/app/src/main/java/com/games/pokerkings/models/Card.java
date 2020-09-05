package com.games.pokerkings.models;

public class Card {

    private int id;

    public Card() {
        this.id = 0;
    }

    public Card(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return "Unknown";
    }

}
