package com.kntronov.makespace.infrastructure.db;

import com.kntronov.makespace.infrastructure.errors.DataSourceConfigurationError;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;

public class PooledDataSource {
    private final HikariDataSource dataSource;

    public PooledDataSource(HikariConfig config) {
        registerDriver();
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);
    }

    private void registerDriver() {
        var myDriver = new org.postgresql.Driver();
        try {
            DriverManager.registerDriver(myDriver);
        } catch (SQLException e) {
            throw new DataSourceConfigurationError("Unable to load driver " + myDriver);
        }
    }

    public LeanConnection getLeanConnection() {
        return new LeanConnection(dataSource);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
