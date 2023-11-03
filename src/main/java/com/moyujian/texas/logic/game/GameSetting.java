package com.moyujian.texas.logic.game;

import com.moyujian.texas.constants.Constants;
import lombok.Data;

@Data
public class GameSetting {

    private int accessChipsNum = 360;

    private int minLargeBet = 10;

    private int maxBet = UNLIMITED_MAX_BET;

    public static final int UNLIMITED_MAX_BET = Integer.MAX_VALUE;
}
