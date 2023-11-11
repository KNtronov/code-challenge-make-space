package com.kntronov.makespace.config;

import com.kntronov.makespace.config.errors.MissingConfigKeyException;
import com.kntronov.makespace.config.errors.UnableToParseConfigValueTypeException;

import java.util.function.Function;

/**
 * Utility class used to load the application configuration from env variables.
 */
public class ConfigLoader {
    private ConfigLoader() {
    }

    public static AppConfig loadConfigFromEnvVariables() {
        final var dbConfig = new AppConfig.DBConfig(
                getOrFail("POSTGRES_URL"),
                getOrFail("POSTGRES_USERNAME"),
                getOrFail("POSTGRES_PASSWORD")
        );
        final var serverConfig = new AppConfig.ServerConfig(
                getOrFail("SERVER_PORT", Integer::parseInt)
        );
        return new AppConfig(dbConfig, serverConfig);
    }

    private static String getOrFail(String envConfigKey) {
        return getOrFail(envConfigKey, Function.identity());
    }

    private static <T> T getOrFail(String envConfigKey, Function<String, T> mapper) {
        var variable = System.getenv(envConfigKey);
        if (variable != null) {
            try {
                return mapper.apply(variable);
            } catch (RuntimeException e) {
                throw new UnableToParseConfigValueTypeException(envConfigKey, variable);
            }
        } else {
            throw new MissingConfigKeyException(envConfigKey);
        }
    }
}
