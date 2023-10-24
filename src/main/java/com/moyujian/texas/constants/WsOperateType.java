package com.moyujian.texas.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WsOperateType {

    // receive
    OPERATE(1),
    GET_GAME_SNAPSHOT(2),
    LEAVE_AFTER_DIE(3),

    // send
    FLUSH_GAME_SNAPSHOT(11),
    RECONNECT_INTO_GAME(12),
    GAME_START(13),
    GAME_OVER(14),
    TURN_OVER(15),
    NEXT_TURN(16),
    IN_TURN_OPERATE(17),
    FLUSH_USER_LIST(18),
    ACCESSIBLE_OPERATES(19)
    ;

    private final int serial;

    public static WsOperateType getBySerial(int serial) {
        for (WsOperateType wsOperateType : WsOperateType.values()) {
            if (wsOperateType.serial == serial) {
                return wsOperateType;
            }
        }
        return null;
    }
}
