package com.kntronov.makespace.config;

/**
 * AppConfig holds all the application configuration values.
 *
 * @param dbConfig     database configuration
 * @param serverConfig server configuration
 */
public record AppConfig(
        DBConfig dbConfig,
        ServerConfig serverConfig
) {
    /**
     * DBConfig holds configuration related to the the database connection.
     *
     * @param url      database connection  url
     * @param username database connection user
     * @param password database connection password
     */
    public record DBConfig(
            String url,
            String username,
            String password
    ) {
    }

    /**
     * ServerConfig holds configuration related to the http server.
     *
     * @param port port the http server listens on
     */
    public record ServerConfig(
            int port
    ) {
    }
}


