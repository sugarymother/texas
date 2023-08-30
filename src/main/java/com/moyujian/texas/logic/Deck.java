package com.moyujian.texas.logic;

import java.util.LinkedList;
import java.util.Random;

public class Deck {

    private final LinkedList<Card> cards = new LinkedList<>();
    private Random random;

    private static final Random RAND_FACTOR = new Random(System.currentTimeMillis());


    public void refresh() {
        cards.clear();
        for (int suit = 1; suit <= 4; suit++) {
            for (int number = 2; number <= 14; number++) {
                cards.add(new Card(number, suit));
            }
        }
        random = new Random(System.currentTimeMillis() - RAND_FACTOR.nextLong(8848L));
    }

    public Card randomDraw() {
        int i = random.nextInt(cards.size());
        Card card = cards.get(i);
        cards.remove(i);
        return card;
    }
}
