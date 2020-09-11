package com.games.pokerkings.data;

import androidx.annotation.Nullable;

import com.games.pokerkings.data.models.User;


public class InitialGameDataResult {
    @Nullable
    private String error;
    private Boolean isDataValid;
    private Integer userIndex;
    private Integer numberOfPlayers;
    private Integer currentMinimum;
    private Integer currentPlayerIndex;
    private Integer startMoney;
    private Integer card1;
    private Integer card2;
    private Integer table1;
    private Integer table2;
    private Integer table3;


    public InitialGameDataResult(@Nullable String error) {
        this.error = error;
        this.isDataValid = false;
    }

    public InitialGameDataResult(Boolean isDataValid, Integer userIndex, Integer numberOfPlayers, Integer currentMinimum, Integer currentPlayerIndex, Integer startMoney, Integer card1, Integer card2, Integer table1, Integer table2, Integer table3) {
        this.error = null;
        this.isDataValid = isDataValid;
        this.userIndex = userIndex;
        this.numberOfPlayers = numberOfPlayers;
        this.currentMinimum = currentMinimum;
        this.currentPlayerIndex = currentPlayerIndex;
        this.startMoney = startMoney;
        this.card1 = card1;
        this.card2 = card2;
        this.table1 = table1;
        this.table2 = table2;
        this.table3 = table3;
    }

    @Nullable
    public String getError() {
        return error;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

    public Integer getUserIndex() {
        return userIndex;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Integer getCurrentMinimum() {
        return currentMinimum;
    }

    public Integer getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Integer getStartMoney() {
        return startMoney;
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
}
