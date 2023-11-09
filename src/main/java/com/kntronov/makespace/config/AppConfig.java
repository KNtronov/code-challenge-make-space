package com.kntronov.makespace.config;

public record AppConfig(
        DBConfig dbConfig,
        ServerConfig serverConfig
) {
    public record DBConfig(
            String url,
            String username,
            String password
    ) {
    }

    public record ServerConfig(
            int port
    ) {
    }
}


