package com.moyujian.texas.logic.game;

import com.moyujian.texas.logic.UserStatus;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.request.OperateModel;
import com.moyujian.texas.response.GameSnapshotVo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game {

    private int accessChipsNum;

    private int minLargeBet;

    private int maxBet;

    private final Deck deck = new Deck();

    private final List<Card> communityArea = new ArrayList<>(5);

    private final List<User> players = new ArrayList<>();

    private final List<PlayerArea> playerAreas = new ArrayList<>();

    private int nextPlayerIdx = 0;
    private int nextDealerIdx = 0;

    private PlayerArea currentOpPlayer = null;
    private PlayerArea dealerPlayer = null;

    private Game() {}

    public static Game run(List<User> players, GameSetting setting) {
        Game game = new Game();
        // 设置game规则数据
        game.accessChipsNum = setting.getAccessChipsNum();
        game.minLargeBet = setting.getMinLargeBet();
        game.maxBet = setting.getMaxBet();
        // 设置玩家
        game.players.addAll(players);
        game.players.forEach(e -> {
            if (UserStatus.ONLINE.equals(e.getStatus())) {
                e.setStatus(UserStatus.GAMING);
            } else {
                e.setStatus(UserStatus.DISCONNECTED);
            }
        });
        // 初始化玩家区域
        for (User player : game.players) {
            game.playerAreas.add(new PlayerArea(player, game.accessChipsNum));
        }

        return game;
    }

    public void restart() {
        // 重置所有player
        for (PlayerArea playerArea : playerAreas) {
            playerArea.reset();
        }
        // 洗牌
        deck.shuffle();
        // 发牌
        for (PlayerArea playerArea : playerAreas) {
            playerArea.reset();
            playerArea.setHand1(deck.draw());
            playerArea.setHand2(deck.draw());
        }
        // 公共区清空
        communityArea.clear();
        // 重置指针
        dealerPlayer = playerAreas.get(nextDealerIdx);
        currentOpPlayer = dealerPlayer;
        nextDealerIdx = (nextDealerIdx + 1) % playerAreas.size();
        nextPlayerIdx = nextDealerIdx;
    }

    public void settle() {
        int totalChips = 0;
        List<PlayerArea> notFoldPlayers = new ArrayList<>();
        for (PlayerArea playerArea : playerAreas) {
            // 总和池内chips
            totalChips += playerArea.getBet();
            if (!playerArea.isFold()) {
                // 未弃牌，则先总合，之后比较结算
                notFoldPlayers.add(playerArea);
            }
        }
        List<PlayerArea> winners = checkWinner(notFoldPlayers);

        int avgChips = totalChips / winners.size();
        int leftChips = totalChips - avgChips;
        winners.sort(Comparator.comparing(PlayerArea::getBet).reversed());
        for (PlayerArea winner : winners) {
            winner.addChips(avgChips + leftChips-- > 0 ? 1 : 0);
        }
    }

    public void inTurnOperate(OperateModel operate) {
        // TODO
    }

    public GameSnapshotVo getSnapshot() {
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
}
