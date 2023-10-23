package com.moyujian.texas.logic.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyujian.texas.logic.UserStatus;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameSnapshot {

    private List<PlayerSnapshot> players;

    private List<CardSnapshot> publicCards;

    private String round;

    private int currentPlayerIdx;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PlayerSnapshot {
        private String username;
        private int chips;
        private int bet;
        private CardSnapshot hand1;
        private CardSnapshot hand2;
        private boolean alive;
        private boolean disconnected;
        private boolean leave;
        private boolean win;
        private boolean fold;
        private boolean allin;
        private String lastOperate;
        private ResultSnapshot finalCardType;

        public static PlayerSnapshot fromPlayerArea(PlayerArea playerArea) {
            PlayerSnapshot playerSnapshot = new PlayerSnapshot();

            playerSnapshot.username = playerArea.getUser().getUsername();
            playerSnapshot.chips = playerArea.getChips();
            playerSnapshot.bet = playerArea.getBet();
            if (playerArea.getHand1() != null) {
                playerSnapshot.hand1 = CardSnapshot.fromCard(playerArea.getHand1());
            }
            if (playerArea.getHand2() != null) {
                playerSnapshot.hand2 = CardSnapshot.fromCard(playerArea.getHand2());
            }
            playerSnapshot.alive = playerArea.isAlive();
            playerSnapshot.disconnected = UserStatus.DISCONNECTED.equals(playerArea.getUser().getStatus());
            playerSnapshot.leave = playerArea.isLeave();
            playerSnapshot.win = playerArea.isWin();
            playerSnapshot.fold = playerArea.isFold();
            playerSnapshot.allin = playerArea.isAllin();
            if (playerArea.getLastOperate() != null) {
                playerSnapshot.lastOperate = playerArea.getLastOperate().getType();
            }
            if (playerArea.getCheckResult() != null) {
                playerSnapshot.finalCardType = ResultSnapshot.fromResult(playerArea.getCheckResult());
            }
            return playerSnapshot;
        }
    }

    @Data
    public static class CardSnapshot {
        private String number;
        private String suit;

        public static CardSnapshot fromCard(Card card) {
            CardSnapshot cardSnapshot = new CardSnapshot();
            if (card.isTopUp()) {
                cardSnapshot.number = CardNumber.getBySerial(card.getNumber()).getNumber();
                cardSnapshot.suit = CardSuit.getBySerial(card.getSuit()).getSuit();
            } else {
                cardSnapshot.number = CardNumber.UNKNOWN.getNumber();
                cardSnapshot.suit = CardSuit.UNKNOWN.getSuit();
            }
            return cardSnapshot;
        }
    }

    @Data
    public static class ResultSnapshot {
        private List<CardSnapshot> cards;
        private String type;

        public static ResultSnapshot fromResult(CardChecker.Result result) {
            ResultSnapshot resultSnapshot = new ResultSnapshot();
            resultSnapshot.cards = result.getCards().stream()
                    .map(CardSnapshot::fromCard)
                    .toList();
            resultSnapshot.type = result.getType();
            return resultSnapshot;
        }
    }
}
