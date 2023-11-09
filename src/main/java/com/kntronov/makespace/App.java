package com.kntronov.makespace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kntronov.makespace.application.Routes;
import com.kntronov.makespace.config.AppConfig;
import com.kntronov.makespace.config.ConfigLoader;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.json.JavalinJackson;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger requestLogger = LoggerFactory.getLogger("request-logger");

    public static void main(String[] args) {
        var config = ConfigLoader.loadConfigFromEnvVariables();
        var context = new AppContext(config);
        runMigrations(context.getDataSource());
        startServer(config.serverConfig(), context);
    }

    private static void runMigrations(PooledDataSource pooledDataSource) {
        var flyway =
                Flyway.configure()
                        .dataSource(pooledDataSource.getDataSource())
                        .locations("db/migration")
                        .load();
        flyway.migrate();
    }

    private static void startServer(AppConfig.ServerConfig serverConfig, AppContext context) {
        Javalin.create(App::configureJavalin)
                .routes(() -> Routes.configureRoutes(context))
                .exception(Exception.class, Routes::configureExceptionHandling)
                .start(serverConfig.port());
    }

    private static void configureJavalin(JavalinConfig config) {
        var jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        Routes.configureConverters();
        config.jsonMapper(new JavalinJackson(jsonMapper));
        config.requestLogger.http((ctx, ms) -> requestLogger.info(
                "[request] {} {} {}",
                ctx.path(), ctx.queryString(), ctx.body()
        ));
    }
}
