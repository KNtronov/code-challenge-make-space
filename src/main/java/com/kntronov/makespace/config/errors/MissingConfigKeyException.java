package com.kntronov.makespace.config.errors;

public class MissingConfigKeyException extends RuntimeException {
    public MissingConfigKeyException(String key) {
        super("missing mandatory configuration for key " + key);
    }
}
