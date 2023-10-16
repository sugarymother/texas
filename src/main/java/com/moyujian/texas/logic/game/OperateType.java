package com.moyujian.texas.logic.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateType {

    CHECK(1),
    CALL(2),
    RAISE(3),
    FOLD(4),
    ALLIN(5)
    ;

    private final int serial;

    public static OperateType getBySerial(int serial) {
        for (OperateType operateType : OperateType.values()) {
            if (operateType.serial == serial) {
                return operateType;
            }
        }
        return null;
    }
}
