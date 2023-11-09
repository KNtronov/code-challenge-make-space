package com.kntronov.makespace.infrastructure.errors;

public class DataSourceConfigurationException extends RuntimeException {
    public DataSourceConfigurationException(String message) {
        super(message);
    }
}
