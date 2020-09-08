package com.games.pokerkings.data.models;

import java.util.ArrayList;
import java.util.List;

public class Game {
    
    private Integer playingUsers;
    private Integer readyUsers;
    private Integer minimumBet;
    private Integer betToMatch;
    private Integer roundJackpot;
    private List<Integer> playersQueue;
    private List<Integer> playersCurrentlyCompeting;
    private List<Integer> playersCurrentlyInRoom;


    public Game() {
        playingUsers = 0;
        readyUsers = 0;
        minimumBet = 0;
        betToMatch = 0;
        roundJackpot = 0;
        this.playersQueue = new ArrayList<>();
        this.playersCurrentlyCompeting = new ArrayList<>();
        this.playersCurrentlyInRoom = new ArrayList<>();
    }

    public Integer getPlayingUsers() {
        return playingUsers;
    }

    public void setPlayingUsers(Integer playingUsers) {
        this.playingUsers = playingUsers;
    }

    public Integer getReadyUsers() {
        return readyUsers;
    }

    public void setReadyUsers(Integer readyUsers) {
        this.readyUsers = readyUsers;
    }

    public Integer getMinimumBet() {
        return minimumBet;
    }

    public void setMinimumBet(Integer minimumBet) {
        this.minimumBet = minimumBet;
    }

    public Integer getBetToMatch() {
        return betToMatch;
    }

    public void setBetToMatch(Integer betToMatch) {
        this.betToMatch = betToMatch;
    }

    public Integer getRoundJackpot() {
        return roundJackpot;
    }

    public void setRoundJackpot(Integer roundJackpot) {
        this.roundJackpot = roundJackpot;
    }

    public List<Integer> getPlayersQueue() {
        return playersQueue;
    }

    public void setPlayersQueue(List<Integer> playersQueue) {
        this.playersQueue = playersQueue;
    }

    public List<Integer> getPlayersCurrentlyCompeting() {
        return playersCurrentlyCompeting;
    }

    public void setPlayersCurrentlyCompeting(List<Integer> playersCurrentlyCompeting) {
        this.playersCurrentlyCompeting = playersCurrentlyCompeting;
    }

    public List<Integer> getPlayersCurrentlyInRoom() {
        return playersCurrentlyInRoom;
    }

    public void setPlayersCurrentlyInRoom(List<Integer> playersCurrentlyInRoom) {
        this.playersCurrentlyInRoom = playersCurrentlyInRoom;
    }
}
