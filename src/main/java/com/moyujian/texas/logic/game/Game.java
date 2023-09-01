package com.moyujian.texas.logic.game;

import com.moyujian.texas.constants.UserStatus;
import com.moyujian.texas.logic.User;

import java.util.ArrayList;
import java.util.List;

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

    private Game() {}

    public static Game startUp(List<User> players, GameSetting setting) {
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

}
