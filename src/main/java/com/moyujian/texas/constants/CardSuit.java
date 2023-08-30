package com.moyujian.texas.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardSuit {
    //Hearts, Diamonds, Clubs, Spades
    HEARTS(1, "♥"),
    DIAMONDS(2, "♦"),
    CLUBS(3, "♣"),
    SPADES(4, "♠"),
    ;

    private final int serial;
    private final String suit;
}
