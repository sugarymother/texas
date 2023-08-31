package com.moyujian.texas.logic.game;

import lombok.Data;

@Data
public class Card {

    private int number;

    private int suit;

    private boolean topUp = false;

    public Card(int number, int suit) {
        this.number = number;
        this.suit = suit;
    }
}
