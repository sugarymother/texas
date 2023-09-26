package com.moyujian.texas.logic.game;

import com.moyujian.texas.constants.UserStatus;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.response.GameSnapshotVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Game {

    private int accessChipsNum;

    private int minLargeBet;

    private int maxBet;

    private final Deck deck = new Deck();

    private final List<Card> communityArea = new ArrayList<>(5);

    private final List<User> players = new ArrayList<>();

    private final List<PlayerArea> playerAreas = new ArrayList<>();

    private int loopIdx = 0;

    private int loopNum = 0;

    private int playerIdx = 0;

    private PlayerArea currentOpPlayer = null;

    private Game() {}

    public static Game run(List<User> players, GameSetting setting) {
        Game game = new Game();
        // 设置game规则数据
        game.accessChipsNum = setting.getAccessChipsNum();
        game.minLargeBet = setting.getMinLargeBet();
        game.maxBet = setting.getMaxBet();
        // 设置玩家
        game.players.addAll(players);
        game.players.forEach(e -> e.setStatus(UserStatus.GAMING));
        // TODO 考虑若启动时user断线将会产生什么影响，应如何解决
        // 初始化玩家区域
        for (User player : game.players) {
            game.playerAreas.add(new PlayerArea(player, game.accessChipsNum));
        }

        return game;
    }

    public void restart() {
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
        loopIdx = 0;
        loopNum = 0;
        playerIdx = 0;
        currentOpPlayer = playerAreas.get(0);
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

        // 重置所有player
        for (PlayerArea playerArea : playerAreas) {
            playerArea.reset();
        }
    }

    public void inTurnOperate() {

    }

    public GameSnapshotVo getSnapshot() {
        return null;
    }

    public List<PlayerArea> checkWinner(List<PlayerArea> players) {
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
            } else {
                break;
            }
        }
        return winners;
    }
}
