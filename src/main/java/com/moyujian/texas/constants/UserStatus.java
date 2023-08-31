package com.moyujian.texas.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    ONLINE(1, "online"),
    GAMING(2, "gaming"),
    DISCONNECTED(3, "disconnected")
    ;

    private final int serial;
    private final String status;
}
