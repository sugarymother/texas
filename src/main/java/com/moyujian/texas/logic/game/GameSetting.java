package com.moyujian.texas.logic.game;

import com.moyujian.texas.constants.Constants;
import lombok.Data;

@Data
public class GameSetting {

    private int accessChipsNum = 350;

    private int minLargeBet = 10;

    private int maxBet = Constants.UNLIMITED_LARGE_BET;
}
