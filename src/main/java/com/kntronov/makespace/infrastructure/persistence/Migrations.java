package com.kntronov.makespace.infrastructure.persistence;

import org.flywaydb.core.Flyway;

public class Migrations {

    private Migrations() {
    }

    public static void performMigrations() {
        var flyway =
                Flyway.configure()
                        .dataSource(
                                System.getenv("POSTGRES_URL"),
                                System.getenv("POSTGRES_USERNAME"),
                                System.getenv("POSTGRES_PASSWORD")
                        )
                        .locations("db/migration")
                        .load();
        flyway.migrate();
    }
}
