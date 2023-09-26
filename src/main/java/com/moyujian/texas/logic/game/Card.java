package com.moyujian.texas.logic.game;

import lombok.Data;

@Data
public class Card implements Comparable<Card> {

    private int number;

    private int suit;

    private boolean topUp = false;

    public Card(int number, int suit) {
        this.number = number;
        this.suit = suit;
    }

    public Card(CardNumber cardNumber, CardSuit cardSuit) {
        number = cardNumber.getSerial();
        suit = cardSuit.getSerial();
    }

    @Override
    public int compareTo(Card other) {
        return (this.number - other.number) * 100 + (this.suit - other.suit);
    }
}
