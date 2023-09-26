package com.moyujian.texas.logic.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardNumber {

    NUM_2(2, "2", 1),
    NUM_3(3, "3", 2),
    NUM_4(4, "4", 3),
    NUM_5(5, "5", 4),
    NUM_6(6, "6", 5),
    NUM_7(7, "7", 6),
    NUM_8(8, "8", 7),
    NUM_9(9, "9", 8),
    NUM_10(10, "10", 9),
    NUM_JACK(11, "J", 10),
    NUM_QUEEN(12, "Q", 11),
    NUM_KING(13, "K", 12),
    NUM_ACE(14, "A", 13),
    ;

    private final int serial;
    private final String number;
    private final int weight;

    public static int getWeightBySerial(int serial) {
        return serial - 1;
    }
}
