package com.kntronov.makespace.application;

import com.kntronov.makespace.config.ConfigLoader;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import org.flywaydb.core.Flyway;

public class Launcher {

    public static void main(String[] args) {
        var config = ConfigLoader.loadConfigFromEnvVariables();
        var context = new AppContext(config);
        var app = new JavalinApp(context, config.serverConfig());
        runMigrations(context.getDataSource());
        app.start();
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
