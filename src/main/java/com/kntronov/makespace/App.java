package com.kntronov.makespace;

import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import com.kntronov.makespace.infrastructure.repositories.SystemStateRepositoryImpl;
import com.zaxxer.hikari.HikariConfig;
import org.flywaydb.core.Flyway;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        var dataSource = setUpDatabaseDataSource();
        runMigrations(dataSource);
    }

    private static PooledDataSource setUpDatabaseDataSource() {
        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(System.getenv("POSTGRES_URL"));
        hikariConfig.setUsername(System.getenv("POSTGRES_USERNAME"));
        hikariConfig.setPassword(System.getenv("POSTGRES_PASSWORD"));
        return new PooledDataSource(hikariConfig);
    }

    private static void runMigrations(PooledDataSource pooledDataSource) {
        var flyway =
                Flyway.configure()
                        .dataSource(pooledDataSource.getDataSource())
                        .locations("db/migration")
                        .load();
        flyway.migrate();
    }
}
