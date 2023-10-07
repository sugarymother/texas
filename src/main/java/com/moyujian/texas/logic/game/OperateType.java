package com.moyujian.texas.logic.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateType {

    CHECK(1, "check"),
    CALL(2, "call"),
    RAISE(3, "raise"),
    FOLD(4, "fold"),
    ALLIN(5, "allin")
    ;

    private final int serial;
    private final String type;
}
