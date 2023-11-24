package com.kntronov.makespace.application.routes;

import com.kntronov.makespace.application.AppContext;

import java.time.LocalDate;
import java.time.LocalTime;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * DSL definition for /rooms routes.
 */
public class RoomRoutes {
    private RoomRoutes() {
    }

    public static void configure(AppContext context) {
        path("rooms", () -> {
            get("available", ctx -> {
                final var date = ctx.queryParamAsClass("date", LocalDate.class).getOrThrow(RouteCommons::createBadRequestException);
                final var start = ctx.queryParamAsClass("from", LocalTime.class).getOrThrow(RouteCommons::createBadRequestException);
                final var end = ctx.queryParamAsClass("to", LocalTime.class).getOrThrow(RouteCommons::createBadRequestException);
                final var response = context.roomsController().getAvailableRooms(date, start, end);
                ctx.status(200).json(response);
            });
        });
    }
}
