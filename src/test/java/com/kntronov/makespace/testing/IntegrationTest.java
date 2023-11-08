package com.kntronov.makespace.testing;

import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import com.zaxxer.hikari.HikariConfig;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class IntegrationTest {

    @SuppressWarnings("rawtypes")
    private final PostgreSQLContainer container = new PostgreSQLContainer<>("postgres");

    private PooledDataSource dataSource;

    @BeforeEach
    void setUp() {
        container.start();
        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(container.getJdbcUrl());
        hikariConfig.setUsername(container.getUsername());
        hikariConfig.setPassword(container.getPassword());
        dataSource = new PooledDataSource(hikariConfig);
        var flyway =
                Flyway.configure()
                        .dataSource(dataSource.getDataSource())
                        .locations("db/migration")
                        .load();
        flyway.migrate();
    }

    @AfterEach
    void cleanUp() {
        container.stop();
    }

    public PooledDataSource getDataSource() {
        return dataSource;
    }
}
