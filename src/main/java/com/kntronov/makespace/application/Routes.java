package com.kntronov.makespace.application;

import com.kntronov.makespace.AppContext;
import com.kntronov.makespace.application.errors.HttpError;
import com.kntronov.makespace.application.schema.CreateBookingRequest;
import com.kntronov.makespace.application.schema.ErrorResponse;
import io.javalin.http.Context;
import io.javalin.validation.JavalinValidation;
import io.javalin.validation.ValidationError;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * A wrapper over the Javalin DSL that defines roots and their handlers.
 */
public class Routes {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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


    public static void configureExceptionHandling(Exception e, Context context) {
        switch (e) {
            case HttpError error -> context.status(error.code()).json(createErrorResponse(error));
            default -> createErrorResponse(new HttpError.InternalServerErrorException());
        }
    }

    public static void configureConverters() {
        JavalinValidation.register(LocalDate.class, s -> LocalDate.parse(s, dateFormatter));
        JavalinValidation.register(LocalTime.class, s -> LocalTime.parse(s, timeFormatter));
        JavalinValidation.register(UUID.class, UUID::fromString);
    }

    private static ErrorResponse createErrorResponse(HttpError error) {
        return new ErrorResponse(
                error.code(),
                error.errorName(),
                error.message()
        );
    }
}
