package com.moyujian.texas.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    ONLINE(1, "online"),
    GAMING(2, "gaming"),
    OFFLINE(0, "offline")
    ;

    private final int serial;
    private final String status;
}
