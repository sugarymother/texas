package com.moyujian.texas.logic.game;

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

    private Game() {}

    public Game startUp(List<User> players, GameSetting setting) {
        Game game = new Game();
        // 设置game规则数据
        accessChipsNum = setting.getAccessChipsNum();
        minLargeBet = setting.getMinLargeBet();
        maxBet = setting.getMaxBet();

        return game;
    }

}
