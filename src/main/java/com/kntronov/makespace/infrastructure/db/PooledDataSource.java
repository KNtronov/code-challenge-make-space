package com.kntronov.makespace.infrastructure.db;

import com.kntronov.makespace.infrastructure.errors.DataSourceConfigurationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A wrapper around a pooled DB connection that exposes LeanConnection.
 */
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
            throw new DataSourceConfigurationException("Unable to load driver " + myDriver);
        }
    }

    public LeanConnection getLeanConnection() {
        return new LeanConnection(dataSource);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
