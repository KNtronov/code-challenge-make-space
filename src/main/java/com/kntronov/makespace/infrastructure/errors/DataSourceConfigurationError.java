package com.kntronov.makespace.infrastructure.errors;

public class DataSourceConfigurationError extends RuntimeException {
    public DataSourceConfigurationError(String message) {
        super(message);
    }
}
