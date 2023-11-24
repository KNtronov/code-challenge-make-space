package com.kntronov.makespace.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kntronov.makespace.application.errors.HttpError;
import com.kntronov.makespace.application.routes.Routes;
import com.kntronov.makespace.application.schema.ErrorResponse;
import com.kntronov.makespace.config.AppConfig;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.JavalinValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class JavalinApp {

    private static final Logger applicationLogger = LoggerFactory.getLogger("application-logger");

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private final AppConfig.ServerConfig config;
    private final Javalin javalin;

    public JavalinApp(AppContext context, AppConfig.ServerConfig config) {
        this.config = config;
        this.javalin = Javalin.create(this::configureJavalin)
                .routes(() -> Routes.configureRoutes(context))
                .exception(Exception.class, this::configureExceptionHandling)
                .after(ctx -> applicationLogger.info(
                        "[response] {} {} {} {}",
                        ctx.path(), ctx.queryString(), ctx.status(), ctx.result()
                ));
    }

    private static ErrorResponse createErrorResponse(HttpError error) {
        return new ErrorResponse(
                error.code(),
                error.errorName(),
                error.message()
        );
    }

    public Javalin getJavalin() {
        return javalin;
    }

    public void start() {
        this.javalin.start(config.port());
    }

    private void configureJavalin(JavalinConfig config) {
        var jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        configureConverters();
        config.jsonMapper(new JavalinJackson(jsonMapper));
        config.requestLogger.http((ctx, ms) -> applicationLogger.info(
                "[request] {} {} {}",
                ctx.path(), ctx.queryString(), ctx.body()
        ));
    }

    private void configureExceptionHandling(Exception e, Context context) {
        switch (e) {
            case HttpError error -> context.status(error.code()).json(createErrorResponse(error));
            default -> createErrorResponse(new HttpError.InternalServerErrorException());
        }
    }

    private void configureConverters() {
        JavalinValidation.register(LocalDate.class, s -> LocalDate.parse(s, dateFormatter));
        JavalinValidation.register(LocalTime.class, s -> LocalTime.parse(s, timeFormatter));
        JavalinValidation.register(UUID.class, UUID::fromString);
    }
}
