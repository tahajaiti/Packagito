package com.kyojin.packagito.core.exception;

public class UserDisabledException extends RuntimeException {

    public UserDisabledException(String username) {
        super("Account is disabled: " + username);
    }
}
