package com.kntronov.makespace.config.errors;

public class UnableToParseConfigValueTypeException extends RuntimeException {
    public UnableToParseConfigValueTypeException(String key, String value) {
        super("unable to parse correct type for key " + key + " and value " + value);
    }
}
