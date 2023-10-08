package com.moyujian.texas.logic.game;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CardChecker {

    @AllArgsConstructor
    private enum CardType {
        HIGH_CARD("high card", 0),
        PAIR("a pair", 1),
        TWO_PAIRS("two pairs", 2),
        THREE_OF_KIND("three of kind", 3),
        STRAIGHT("straight", 4),
        FLUSH("flush", 5),
        FULL_HOUSE("full house", 6),
        FOUR_OF_KIND("four of kind", 7),
        STRAIGHT_FLUSH("STRAIGHT FLUSH", 8),
        ROYAL_FLUSH("!! ROYAL FLUSH !!", 9),
        ;

        private final String type;
        private final int weight;
    }

    private static class WeightCalculator {
        private int weight;

        public WeightCalculator setType(CardType type) {
            weight = type.weight * 14 * 14 * 14 * 14 * 14;
            return this;
        }

        public WeightCalculator setDigit5(int weight) {
            this.weight += weight * 14 * 14 * 14 * 14;
            return this;
        }

        public WeightCalculator setDigit4(int weight) {
            this.weight += weight * 14 * 14 * 14;
            return this;
        }

        public WeightCalculator setDigit3(int weight) {
            this.weight += weight * 14 * 14;
            return this;
        }

        public WeightCalculator setDigit2(int weight) {
            this.weight += weight * 14;
            return this;
        }

        public WeightCalculator setDigit1(int weight) {
            this.weight += weight;
            return this;
        }

        public int calculate() {
            return weight;
        }
    }

    @Data
    public static class Result implements Comparable<Result> {
        private final int weight;
        private final List<Card> cards;
        private final String type;

        public Result(int weight, CardType cardType, List<Card> cards) {
            this.weight = weight;
            this.type = cardType.type;
            this.cards = cards;
        }

        @Override
        public int compareTo(Result o) {
            return this.weight - o.weight;
        }
    }

    public static void check(List<Card> communityCards, PlayerArea player) {
        Result result;

        // 总和全部牌
        List<Card> allCards = new ArrayList<>(communityCards);
        allCards.add(player.getHand1());
        allCards.add(player.getHand2());
        Collections.sort(allCards);

        // 检查牌型
        result = checkStraightFlushOrRoyalFlush(allCards);
        if (result == null) {
            result = checkFourOfKind(allCards);
        }
        if (result == null) {
            result = checkFullHouse(allCards);
        }
        if (result == null) {
            result = checkFlush(allCards);
        }
        if (result == null) {
            result = checkStraight(allCards);
        }
        if (result == null) {
            result = checkThreeOfKind(allCards);
        }
        if (result == null) {
            result = checkPairOrTwoPairs(allCards);
        }
        if (result == null) {
            result = checkHighCard(allCards);
        }

        player.setCheckResult(result);
    }

    private static Result checkFourOfKind(List<Card> cards) {
        //TODO
        return null;
    }

    private static Result checkFullHouse(List<Card> cards) {
        //TODO
        return null;
    }

    private static Result checkStraightFlushOrRoyalFlush(List<Card> cards) {
        //TODO
        return null;
    }

    private static Result checkFlush(List<Card> cards) {
        //TODO
        return null;
    }

    private static Result checkStraight(List<Card> cards) {
        WeightCalculator weightCalculator = new WeightCalculator().setType(CardType.STRAIGHT);
        LinkedList<Card> resultCards = new LinkedList<>();

        int len = 1;
        LinkedList<Card> cardSeries = new LinkedList<>();
        int lastNum = cards.get(0).getNumber();
        cardSeries.addLast(cards.get(0));
        for (int i = 1, n = cards.size(); i < n; i++) {
            Card card = cards.get(i);
            if (card.getNumber() == lastNum + 1) {
                lastNum++;
                len++;
            } else {
                if (len >= resultCards.size()) {
                    resultCards.clear();
                    resultCards.addAll(cardSeries);
                }
                len = 1;
                lastNum = card.getNumber();
                cardSeries.clear();
            }
            cardSeries.addLast(card);
        }

        if (resultCards.size() < 4) {
            return null;
        } else if (resultCards.size() == 4) {
            Card lastCard = cards.get(cards.size() - 1);
            if (resultCards.getFirst().getNumber() == CardNumber.NUM_2.getSerial()
                    && lastCard.getNumber() == CardNumber.NUM_ACE.getSerial()) {
                resultCards.addFirst(lastCard);
            }
            weightCalculator.setDigit5(CardNumber.getWeightBySerial(resultCards.get(1).getNumber()))
                    .setDigit1(1);
        } else {
            resultCards = new LinkedList<>(resultCards.subList(resultCards.size() - 5, resultCards.size()));
            weightCalculator.setDigit5(CardNumber.getWeightBySerial(resultCards.getFirst().getNumber()))
                    .setDigit1(2);
        }
        return new Result(weightCalculator.calculate(), CardType.STRAIGHT, resultCards);
    }

    /**
     * 根据判断顺序，已知牌型肯定没有四条
     */
    private static Result checkThreeOfKind(List<Card> cards) {
        WeightCalculator weightCalculator = new WeightCalculator().setType(CardType.THREE_OF_KIND);
        List<Card> resultCards = new ArrayList<>();

        int maxTok = 0;

        Map<Integer, Integer> numMap = new HashMap<>();
        for (Card card : cards) {
            int num = numMap.getOrDefault(card.getNumber(), 0) + 1;
            if (num >= 3) {
                maxTok = Math.max(maxTok, card.getNumber());
                numMap.remove(card.getNumber());
            } else {
                numMap.put(card.getNumber(), num);
            }
        }

        if (maxTok == 0) {
            return null;
        } else {
            List<Card> highCards = new ArrayList<>();
            for (Card card : cards) {
                if (card.getNumber() == maxTok) {
                    resultCards.add(card);
                } else {
                    highCards.add(card);
                }
            }
            highCards.sort(Comparator.reverseOrder());
            weightCalculator.setDigit5(CardNumber.getWeightBySerial(maxTok))
                    .setDigit2(CardNumber.getWeightBySerial(highCards.get(0).getNumber()))
                    .setDigit1(CardNumber.getWeightBySerial(highCards.get(1).getNumber()));
            resultCards.add(highCards.get(0));
            resultCards.add(highCards.get(1));
            return new Result(weightCalculator.calculate(), CardType.THREE_OF_KIND, resultCards);
        }
    }

    /**
     * 根据判断顺序，已确定牌型肯定没有四条、三条
     */
    private static Result checkPairOrTwoPairs(List<Card> cards) {
        CardType type;
        WeightCalculator weightCalculator = new WeightCalculator();
        List<Card> resultCards = new ArrayList<>();

        Set<Integer> pairSet = new HashSet<>();
        Card preCard = cards.get(0);
        for (int i = 1, n = cards.size(); i < n; i++) {
            Card currCard = cards.get(i);
            if (currCard.getNumber() == preCard.getNumber()) {
                pairSet.add(currCard.getNumber());
            }
            preCard = currCard;
        }

        if (pairSet.isEmpty()) {
            // no pair
            return null;
        }

        List<Integer> highCards = new ArrayList<>();
        for (Card card : cards) {
            if (!pairSet.contains(card.getNumber())) {
                highCards.add(card.getNumber());
            }
        }
        highCards.sort(Comparator.reverseOrder());

        if (pairSet.size() == 1) {
            // one pair
            type = CardType.PAIR;
            int maxPair = pairSet.stream().max(Integer::compareTo).get();
            weightCalculator.setType(type)
                    .setDigit5(CardNumber.getWeightBySerial(maxPair))
                    .setDigit3(CardNumber.getWeightBySerial(highCards.get(0)))
                    .setDigit2(CardNumber.getWeightBySerial(highCards.get(1)))
                    .setDigit1(CardNumber.getWeightBySerial(highCards.get(3)));
            for (Card card : cards) {
                if (card.getNumber() == maxPair || card.getNumber() == highCards.get(0) ||
                        card.getNumber() == highCards.get(1) || card.getNumber() == highCards.get(2)) {
                    resultCards.add(card);
                }
            }
        } else {
            // two pairs
            type = CardType.TWO_PAIRS;
            List<Integer> pairs = new ArrayList<>(pairSet);
            pairs.sort(Comparator.reverseOrder());
            weightCalculator.setType(type)
                    .setDigit5(CardNumber.getWeightBySerial(pairs.get(0)))
                    .setDigit4(CardNumber.getWeightBySerial(pairs.get(1)))
                    .setDigit1(CardNumber.getWeightBySerial(highCards.get(0)));
            for (Card card : cards) {
                if (card.getNumber() == pairs.get(0) || card.getNumber() == pairs.get(1) ||
                        card.getNumber() == highCards.get(0)) {
                    resultCards.add(card);
                }
            }
        }
        return new Result(weightCalculator.calculate(), type, resultCards);
    }

    private static Result checkHighCard(List<Card> cards) {
        cards.sort(Comparator.reverseOrder());
        List<Card> resultCards = cards.subList(0, 5);
        int weight = new WeightCalculator().setType(CardType.HIGH_CARD)
                .setDigit5(CardNumber.getWeightBySerial(resultCards.get(0).getNumber()))
                .setDigit4(CardNumber.getWeightBySerial(resultCards.get(1).getNumber()))
                .setDigit3(CardNumber.getWeightBySerial(resultCards.get(2).getNumber()))
                .setDigit2(CardNumber.getWeightBySerial(resultCards.get(3).getNumber()))
                .setDigit1(CardNumber.getWeightBySerial(resultCards.get(4).getNumber()))
                .calculate();
        return new Result(weight, CardType.HIGH_CARD, resultCards);
    }
}
