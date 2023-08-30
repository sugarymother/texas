package com.moyujian.texas.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardNumber {

    NUM_2(2, "2"),
    NUM_3(3, "3"),
    NUM_4(4, "4"),
    NUM_5(5, "5"),
    NUM_6(6, "6"),
    NUM_7(7, "7"),
    NUM_8(8, "8"),
    NUM_9(9, "9"),
    NUM_10(10, "10"),
    NUM_JACK(11, "J"),
    NUM_QUEEN(12, "Q"),
    NUM_KING(13, "K"),
    NUM_ACE(14, "A"),
    ;

    private final int serial;
    private final String number;
}
