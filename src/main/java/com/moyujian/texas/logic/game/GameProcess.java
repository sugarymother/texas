package com.moyujian.texas.logic.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameProcess {

    // No pre-flop round

    FLOP(1, "flop round"),
    TURN(2, "turn round"),
    RIVER(3, "river round")
    ;

    private final int serial;
    private final String round;
}
