package com.moyujian.texas.logic.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardSuit {
    //Hearts, Diamonds, Clubs, Spades
    UNKNOWN(0, "?"),
    HEARTS(1, "♥"),
    DIAMONDS(2, "♦"),
    CLUBS(3, "♣"),
    SPADES(4, "♠"),
    ;

    private final int serial;
    private final String suit;

    public static CardSuit getBySerial(int serial) {
        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit.serial == serial) {
                return cardSuit;
            }
        }
        return UNKNOWN;
    }
}
