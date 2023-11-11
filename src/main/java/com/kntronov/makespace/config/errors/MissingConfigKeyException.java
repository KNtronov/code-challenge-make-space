package com.kntronov.makespace.config.errors;

/**
 * Exception to be raised when a mandatory configuration key is missing.
 */
public class MissingConfigKeyException extends RuntimeException {
    public MissingConfigKeyException(String key) {
        super("missing mandatory configuration for key " + key);
    }
}
