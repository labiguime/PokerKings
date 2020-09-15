package com.games.pokerkings.data;

public class DisconnectionType {
    Integer type;
    Integer myIndex;
    Integer numberOfPlayers;
    Integer disconnectedPlayer;

    public DisconnectionType(Integer type) {
        this.type = type;
    }

    public DisconnectionType(Integer type, Integer myIndex, Integer numberOfPlayers, Integer disconnectedPlayer) {
        this.type = type;
        this.myIndex = myIndex;
        this.numberOfPlayers = numberOfPlayers;
        this.disconnectedPlayer = disconnectedPlayer;
    }

    public Integer getType() {
        return type;
    }

    public Integer getMyIndex() {
        return myIndex;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Integer getDisconnectedPlayer() {
        return disconnectedPlayer;
    }
}
