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
    OPEN_GAME(4),
    ENTER_ROOM(5),
    LEAVE_ROOM(6),

    // send
    FLUSH_GAME_SNAPSHOT(11),
    RECONNECT_INTO_GAME(12),
    GAME_START(13),
    GAME_OVER(14),
    TURN_OVER(15),
    IN_TURN_OPERATE(16),
    FLUSH_USER_LIST(17),
    FLUSH_ROOM_SNAPSHOT(18),
    START_GAME_FAILED(19)
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
