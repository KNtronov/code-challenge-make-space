package com.kntronov.makespace.infrastructure.errors;

/**
 * Exception raised if data source configuration fails.
 */
public class DataSourceConfigurationException extends RuntimeException {
    public DataSourceConfigurationException(String message) {
        super(message);
    }
}
