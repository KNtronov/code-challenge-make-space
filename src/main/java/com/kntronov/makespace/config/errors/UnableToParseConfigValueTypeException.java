package com.kntronov.makespace.config.errors;

/**
 * Exception to be raised when a configuration value exists but is malformed.
 */
public class UnableToParseConfigValueTypeException extends RuntimeException {
    public UnableToParseConfigValueTypeException(String key, String value) {
        super("unable to parse correct type for key " + key + " and value " + value);
    }
}
