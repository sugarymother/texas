package com.moyujian.texas.logic.game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Deck {

    private final LinkedList<Card> cards = new LinkedList<>();

    private int cardIdx = 0;

    private static final Random RAND_FACTOR = new Random(System.currentTimeMillis());


    public void shuffle() {
        cards.clear();
        cardIdx = 0;
        for (int suit = 1; suit <= 4; suit++) {
            for (int number = 2; number <= 14; number++) {
                cards.add(new Card(number, suit));
            }
        }
        Random random = new Random(System.currentTimeMillis() - RAND_FACTOR.nextLong(8848L));
        Collections.shuffle(cards, random);
    }

    public Card draw() {
        return cards.get(cardIdx++);
    }
}
