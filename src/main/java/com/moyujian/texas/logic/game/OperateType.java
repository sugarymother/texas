package com.moyujian.texas.logic.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateType {

    CHECK(1, "CHECK"),
    CALL(2, "CALL"),
    RAISE(3, "RAISE"),
    FOLD(4, "FOLD"),
    ALLIN(5, "ALLIN")
    ;

    private final int serial;
    private final String type;

    public static OperateType getBySerial(int serial) {
        for (OperateType operateType : OperateType.values()) {
            if (operateType.serial == serial) {
                return operateType;
            }
        }
        return null;
    }
}
