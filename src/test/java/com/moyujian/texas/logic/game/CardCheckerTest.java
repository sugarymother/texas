package com.moyujian.texas.logic.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CardCheckerTest {

    @Test
    void testCheck() throws JsonProcessingException {
        List<Card> communityCards = new ArrayList<>();
        communityCards.add(new Card(CardNumber.NUM_ACE, CardSuit.CLUBS));
        communityCards.add(new Card(CardNumber.NUM_2, CardSuit.DIAMONDS));
        communityCards.add(new Card(CardNumber.NUM_3, CardSuit.CLUBS));
        communityCards.add(new Card(CardNumber.NUM_10, CardSuit.SPADES));
        communityCards.add(new Card(CardNumber.NUM_7, CardSuit.DIAMONDS));

        PlayerArea player1 = new PlayerArea();
        player1.setHand1(new Card(CardNumber.NUM_4, CardSuit.HEARTS));
        player1.setHand2(new Card(CardNumber.NUM_5, CardSuit.HEARTS));

        PlayerArea player2 = new PlayerArea();
        player2.setHand1(new Card(CardNumber.NUM_ACE, CardSuit.HEARTS));
        player2.setHand2(new Card(CardNumber.NUM_ACE, CardSuit.HEARTS));

        PlayerArea player3 = new PlayerArea();
        player3.setHand1(new Card(CardNumber.NUM_2, CardSuit.HEARTS));
        player3.setHand2(new Card(CardNumber.NUM_3, CardSuit.HEARTS));

        PlayerArea player4 = new PlayerArea();
        player4.setHand1(new Card(CardNumber.NUM_10, CardSuit.HEARTS));
        player4.setHand2(new Card(CardNumber.NUM_6, CardSuit.HEARTS));

        PlayerArea player5 = new PlayerArea();
        player5.setHand1(new Card(CardNumber.NUM_2, CardSuit.HEARTS));
        player5.setHand2(new Card(CardNumber.NUM_2, CardSuit.HEARTS));

        List<PlayerArea> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        players.add(player5);

        ObjectMapper objectMapper = new ObjectMapper();

        long timeLen = System.currentTimeMillis();
        for (PlayerArea player : players) {
            CardChecker.check(communityCards, player);
        }
        timeLen = System.currentTimeMillis() - timeLen;

        players.sort(Comparator.comparing(PlayerArea::getCheckResult).reversed());

        for (PlayerArea player : players) {
            System.out.println(objectMapper.writeValueAsString(player.getCheckResult()));
        }
        System.out.println("time cost: " + timeLen + "ms");
    }
}
