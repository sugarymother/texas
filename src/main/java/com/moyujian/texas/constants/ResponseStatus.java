package com.moyujian.texas.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {

    NOT_SINGED(101, "未创建，请创建用户"),
    NOT_CONNECTED(102, "未连接，请连入"),
    ERROR_OCCURRED(105, "连接发生错误"),
    OLD_CONNECT_ABORT(106, "已在其他地址重新连接，当前连接已断开")
    ;

    private final int status;
    private final String msg;
}
