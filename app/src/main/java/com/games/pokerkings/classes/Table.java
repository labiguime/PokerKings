package com.games.pokerkings.classes;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private List<Card> cards;
    private List<Integer> winners;
    private List<Integer> cardsPool;

    public Table() {
        this.cards = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.cardsPool = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public List<Integer> getWinners() {
        return winners;
    }

    public void setWinners(List<Integer> winners) {
        this.winners = winners;
    }

    public void adWinner(Integer winner) {
        this.winners.add(winner);
    }

    public List<Integer> getCardsPool() {
        return cardsPool;
    }

    public void setCardsPool(List<Integer> cardsPool) {
        this.cardsPool = cardsPool;
    }

    public void addIdToPool(Integer cardId) {
        this.cardsPool.add(cardId);
    }
}
