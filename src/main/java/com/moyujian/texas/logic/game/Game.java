package com.moyujian.texas.logic.game;

import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.UserStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game {

    private int accessChipsNum;

    private int minLargeBet;

    private int maxBet;

    private final Deck deck = new Deck();

    private final List<Card> communityArea = new ArrayList<>(5);

    private final List<PlayerArea> playerAreas = new ArrayList<>();

    private int nextPlayerIdx = 0;
    private int nextDealerIdx = 0;

    private PlayerArea currentOpPlayer = null;
    private PlayerArea dealerPlayer = null;

    private int roundBet = 0;

    private int totalBet = 0;

    private int round = 0;

    private boolean gameOver = false;

    private boolean turnEnd = false;

    public static final int FLOP_ROUND = 0;
    public static final int TURN_ROUND = 1;
    public static final int RIVER_ROUND = 2;

    private Game() {}

    public static Game run(List<User> players, GameSetting setting) {
        Game game = new Game();
        // 设置game规则数据
        game.accessChipsNum = setting.getAccessChipsNum();
        game.minLargeBet = setting.getMinLargeBet();
        game.maxBet = setting.getMaxBet();

        // 设置玩家
        // 初始化玩家区域
        for (User player : players) {
            if (UserStatus.ONLINE.equals(player.getStatus())) {
                player.setStatus(UserStatus.GAMING);
            } else {
                player.setStatus(UserStatus.DISCONNECTED);
            }
            game.playerAreas.add(new PlayerArea(player, game.accessChipsNum));
        }

        return game;
    }

    public void restart() {
        turnEnd = false;

        // 重置所有player
        for (PlayerArea playerArea : playerAreas) {
            playerArea.reset();
        }

        // 洗牌
        deck.shuffle();
        // 发牌
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive()) {
                playerArea.setHand1(deck.draw());
                playerArea.setHand2(deck.draw());
            }
        }
        // 公共区清空
        communityArea.clear();
        // 重置指针
        dealerPlayer = playerAreas.get(nextDealerIdx);
        currentOpPlayer = dealerPlayer;
        do {
            nextDealerIdx = (nextDealerIdx + 1) % playerAreas.size();
        } while (!playerAreas.get(nextDealerIdx).isAlive());
        nextPlayerIdx = nextDealerIdx;

        // 跳过preflop
        round = FLOP_ROUND;
        roundBet = 0;
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive()) {
                playerArea.setBet(minLargeBet);
            }
        }
        totalBet = minLargeBet;
        for (int i = 0, n = 3; i < n; i++) {
            Card drawedCard = deck.draw();
            drawedCard.setTopUp(true);
            communityArea.add(drawedCard);
        }
    }

    public void settle() {
        int totalChips = 0;
        List<PlayerArea> notFoldPlayers = new ArrayList<>();
        for (PlayerArea playerArea : playerAreas) {
            // 总和池内chips
            totalChips += playerArea.getBet();
            if (playerArea.isAlive() && !playerArea.isFold()) {
                // 未弃牌，则先总合，之后比较结算
                notFoldPlayers.add(playerArea);
            }
        }

        // 未弃牌的翻牌
        if (notFoldPlayers.size() > 1) {
            for (PlayerArea notFoldPlayer : notFoldPlayers) {
                notFoldPlayer.getHand1().setTopUp(true);
                notFoldPlayer.getHand2().setTopUp(true);
            }
        }

        List<PlayerArea> winners = checkWinner(notFoldPlayers);

        int avgChips = totalChips / winners.size();
        int leftChips = totalChips - avgChips;
        winners.sort(Comparator.comparing(PlayerArea::getBet).reversed());
        for (PlayerArea winner : winners) {
            winner.addChips(avgChips + leftChips-- > 0 ? 1 : 0);
        }

        // 不足最小bet则直接强制退出
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive() && playerArea.getChips() < minLargeBet) {
                playerArea.die();
            }
        }

        // check game over
        if (alivePlayerNum() <= 1) {
            gameOver();
        }
    }

    public void inTurnOperate(OperateType operateType, int chipsNum) {
        switch (operateType) {
            case CHECK, CALL -> checkAndCallOperate();
            case RAISE -> raiseOperate(chipsNum);
            case ALLIN -> allinOperate();
            case FOLD -> foldOperate();
        }
        currentOpPlayer.setLastOperate(operateType);

        if (!playerNext()) {
            // 圈结束
            if (accessiblePlayerNum() <= 1) {
                // 如果全部player无操作，直接结束轮
                turnEnd();
            } else if (!roundNext()) {
                // 轮结束
                turnEnd();
            }
        } else if (accessiblePlayerNum() <= 1) {
            // 只剩最后一个可操作player，直接结束轮
            turnEnd();
        } else if (UserStatus.DISCONNECTED.equals(currentOpPlayer.getUser().getStatus())) {
            // 下个操作player掉线，自动fold
            inTurnOperate(OperateType.FOLD, 0);
        }
    }

    public GameSnapshot getSnapshot() {
        // TODO
        return null;
    }

    public AccessibleOperate getCurrentUserAccessibleOperate() {
        // TODO
        return null;
    }

    public PlayerArea getCurrentOpPlayer() {
        return currentOpPlayer;
    }

    private List<PlayerArea> checkWinner(List<PlayerArea> players) {
        if (players.size() < 2) {
            return players;
        }

        List<PlayerArea> winners = new ArrayList<>();
        int maxWeight;

        for (PlayerArea player : players) {
            CardChecker.check(communityArea, player);
        }
        players.sort(Comparator.comparing(PlayerArea::getCheckResult).reversed());

        maxWeight = players.get(0).getCheckResult().getWeight();
        for (PlayerArea player : players) {
            if (player.getCheckResult().getWeight() == maxWeight) {
                winners.add(player);
                player.setWin(true);
            } else {
                break;
            }
        }
        return winners;
    }

    private boolean playerNext() {
        PlayerArea nextPlayer = playerAreas.get(nextPlayerIdx);
        if (nextPlayer.equals(dealerPlayer)) {
            // TODO 考虑bet未追平情况
            return false;
        }
        currentOpPlayer = nextPlayer;
        nextPlayerIdx = (nextPlayerIdx + 1) % playerAreas.size();

        if (currentOpPlayer.isAlive() && !currentOpPlayer.isAllin() && !currentOpPlayer.isFold()) {
            return true;
        } else {
            return playerNext();
        }
    }

    private boolean roundNext() {
        roundBet = 0;
        return ++round <= RIVER_ROUND;
    }

    private void checkAndCallOperate() {
        currentOpPlayer.placeBet(totalBet - currentOpPlayer.getBet());
    }

    private void raiseOperate(int bet) {
        int raiseBet = bet + currentOpPlayer.getBet() - totalBet;
        roundBet += raiseBet;
        currentOpPlayer.placeBet(bet);
        totalBet = currentOpPlayer.getBet();
    }

    private void foldOperate() {
        currentOpPlayer.setFold(true);
    }

    private void allinOperate() {
        int raiseBet = currentOpPlayer.getBet() + currentOpPlayer.getChips() - totalBet;
        if (raiseBet > 0) {
            roundBet += raiseBet;
            totalBet += raiseBet;
        }
        currentOpPlayer.placeBet(currentOpPlayer.getChips());
        currentOpPlayer.setAllin(true);
    }

    private void gameOver() {
        gameOver = true;
    }

    private void turnEnd() {
        turnEnd = false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isTurnEnd() {
        return turnEnd;
    }

    private int alivePlayerNum() {
        int alivePlayerNum = 0;
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive()) {
                alivePlayerNum++;
            }
        }
        return alivePlayerNum;
    }

    private int accessiblePlayerNum() {
        int accessiblePlayerNum = 0;
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive() && !playerArea.isFold() && !playerArea.isAllin()) {
                accessiblePlayerNum++;
            }
        }
        return accessiblePlayerNum;
    }
}
