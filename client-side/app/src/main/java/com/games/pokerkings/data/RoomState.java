package com.games.pokerkings.data;

import androidx.annotation.Nullable;

import java.util.List;

public class RoomState {

    @Nullable
    private String error;

    @Nullable
    List<Integer> allCards;

    @Nullable
    Integer winner;

    Boolean hasRoundEnded;
    Integer nextPlayer;
    Integer actionType;
    Integer whoPlayed;
    Integer playerNewMoney;
    Integer playerMoneyChange;
    Integer tableTotal;
    Integer tableCard;
    Integer currentMinimum;
    Integer myIndex;
    Integer nPlayers;
    Boolean isGameOver;
    Integer gameStage;

    public RoomState(Boolean hasRoundEnded, @Nullable List<Integer> allCards, Integer nextPlayer, Integer actionType, Integer whoPlayed, @Nullable Integer winner, Integer playerNewMoney, Integer playerMoneyChange, Integer tableTotal, Integer tableCard, Integer currentMinimum, Integer myIndex, Integer nPlayers, Boolean isGameOver, Integer gameStage) {
        this.error = null;
        this.hasRoundEnded = hasRoundEnded;
        this.allCards = allCards;
        this.nextPlayer = nextPlayer;
        this.actionType = actionType;
        this.whoPlayed = whoPlayed;
        this.winner = winner;
        this.playerNewMoney = playerNewMoney;
        this.playerMoneyChange = playerMoneyChange;
        this.tableTotal = tableTotal;
        this.tableCard = tableCard;
        this.currentMinimum = currentMinimum;
        this.myIndex = myIndex;
        this.nPlayers = nPlayers;
        this.isGameOver = isGameOver;
        this.gameStage = gameStage;
    }

    public RoomState(String error) {
        this.error = error;
    }

    @Nullable
    public String getError() {
        return error;
    }

    @Nullable
    public Integer getWinner() {
        return winner;
    }

    @Nullable
    public List<Integer> getAllCards() {
        return allCards;
    }

    public Integer getGameStage() {
        return gameStage;
    }

    public Boolean getIsGameOver() {
        return isGameOver;
    }
    public Boolean getHasRoundEnded() {
        return hasRoundEnded;
    }

    public Integer getNextPlayer() {
        return nextPlayer;
    }

    public Integer getActionType() {
        return actionType;
    }

    public Integer getWhoPlayed() {
        return whoPlayed;
    }

    public Integer getPlayerNewMoney() {
        return playerNewMoney;
    }

    public Integer getPlayerMoneyChange() {
        return playerMoneyChange;
    }

    public Integer getTableTotal() {
        return tableTotal;
    }

    public Integer getTableCard() {
        return tableCard;
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
}
