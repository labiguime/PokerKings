package com.games.pokerkings.data;

import androidx.annotation.Nullable;

import java.util.List;

public class RoomResults {

    @Nullable
    private String error;

    String message;
    Boolean hasRoundEnded;
    Integer currentMinimum;
    Integer myIndex;
    Integer nPlayers;
    Integer gameStage;
    Integer currentPlayer;
    Integer card1;
    Integer card2;
    Integer table1;
    Integer table2;
    Integer table3;
    List<Integer> winner;
    List<Integer> allCards;
    List<Integer> playersMoney;

    public RoomResults(String message, Boolean hasRoundEnded, Integer currentMinimum, Integer myIndex, Integer nPlayers, Integer gameStage, Integer currentPlayer, Integer card1, Integer card2, Integer table1, Integer table2, Integer table3, List<Integer> winner, List<Integer> allCards, List<Integer> playersMoney) {
        this.error = null;
        this.message = message;
        this.hasRoundEnded = hasRoundEnded;
        this.currentMinimum = currentMinimum;
        this.myIndex = myIndex;
        this.nPlayers = nPlayers;
        this.gameStage = gameStage;
        this.currentPlayer = currentPlayer;
        this.card1 = card1;
        this.card2 = card2;
        this.table1 = table1;
        this.table2 = table2;
        this.table3 = table3;
        this.winner = winner;
        this.allCards = allCards;
        this.playersMoney = playersMoney;
    }

    public RoomResults(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getHasRoundEnded() {
        return hasRoundEnded;
    }

    public Integer getCurrentMinimum() {
        return currentMinimum;
    }

    public Integer getMyIndex() {
        return myIndex;
    }

    public Integer getnPlayers() {
        return nPlayers;
    }

    public Integer getGameStage() {
        return gameStage;
    }

    public Integer getCurrentPlayer() {
        return currentPlayer;
    }

    public Integer getCard1() {
        return card1;
    }

    public Integer getCard2() {
        return card2;
    }

    public Integer getTable1() {
        return table1;
    }

    public Integer getTable2() {
        return table2;
    }

    public Integer getTable3() {
        return table3;
    }

    public List<Integer> getWinner() {
        return winner;
    }

    public List<Integer> getAllCards() {
        return allCards;
    }

    public List<Integer> getPlayersMoney() {
        return playersMoney;
    }

    @Nullable
    public String getError() {
        return error;
    }

}
