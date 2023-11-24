package com.kntronov.makespace.application.routes;

import com.kntronov.makespace.application.AppContext;
import com.kntronov.makespace.application.errors.HttpError;
import com.kntronov.makespace.application.schema.CreateBookingRequest;
import io.javalin.validation.ValidationError;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * A wrapper over the Javalin DSL that defines roots and their handlers.
 */
public class Routes {

    private Routes() {
    }

    public static void configureRoutes(AppContext context) {
        path("api", () -> {
            path("bookings", () -> {
                post("create-best-matching", ctx -> {
                    final var request = CreateBookingRequest.validated(ctx).getOrThrow(Routes::createBadRequest);
                    final var response = context.getBookingsController().bookNextAvailableRoom(request);
                    ctx.status(201).json(response);
                });
                get(ctx -> {
                    final var date = ctx.queryParamAsClass("date", LocalDate.class).getOrThrow(Routes::createBadRequest);
                    final var response = context.getBookingsController().getAllBookings(date);
                    ctx.status(200).json(response);
                });
                path("{id}", () -> {
                    get(ctx -> {
                        final var id = ctx.pathParamAsClass("id", UUID.class).getOrThrow(Routes::createBadRequest);
                        final var response = context.getBookingsController().getBooking(id);
                        ctx.status(200).json(response);
                    });
                    delete(ctx -> {
                        final var id = ctx.pathParamAsClass("id", UUID.class).getOrThrow(Routes::createBadRequest);
                        context.getBookingsController().deleteBooking(id);
                        ctx.status(200);
                    });
                });
            });
            path("rooms", () -> {
                get("available", ctx -> {
                    final var date = ctx.queryParamAsClass("date", LocalDate.class).getOrThrow(Routes::createBadRequest);
                    final var start = ctx.queryParamAsClass("from", LocalTime.class).getOrThrow(Routes::createBadRequest);
                    final var end = ctx.queryParamAsClass("to", LocalTime.class).getOrThrow(Routes::createBadRequest);
                    final var response = context.getRoomsController().getAvailableRooms(date, start, end);
                    ctx.status(200).json(response);
                });
            });
        });
    }

    private static Exception createBadRequest(Map<String, ? extends List<ValidationError<Object>>> stringMap) {
        return new HttpError.BadRequestException(
                stringMap.entrySet()
                        .stream()
                        .flatMap(entry -> entry.getValue()
                                .stream()
                                .map(error -> "[" + entry.getKey() + "] error: " + error.getMessage())
                        )
                        .toList()
        );
    }

}
