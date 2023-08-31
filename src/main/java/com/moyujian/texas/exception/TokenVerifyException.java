package com.moyujian.texas.exception;

public class TokenVerifyException extends Exception {

    public TokenVerifyException(Throwable cause) {
        super("jwt token verify failed.", cause);
    }
}
