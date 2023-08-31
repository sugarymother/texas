package com.moyujian.texas.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {

    NOT_SINGED(101, "未创建，请创建用户"),
    IN_GAME(102, "用户正在game中，请重连"),
    ;

    private final int status;
    private final String msg;
}
