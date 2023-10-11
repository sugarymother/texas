package com.moyujian.texas.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    OFFLINE(0, "offline"),
    ONLINE(1, "online"),
    GAMING(2, "gaming"),
    DISCONNECTED(3, "disconnected")
    ;

    private final int serial;
    private final String status;
}
