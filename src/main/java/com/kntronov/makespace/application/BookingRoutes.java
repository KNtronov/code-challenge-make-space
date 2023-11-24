package com.kntronov.makespace.application;

import com.kntronov.makespace.application.schema.CreateBookingRequest;

import java.time.LocalDate;
import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * DSL definition for /bookings routes.
 */
public class BookingRoutes {
    private BookingRoutes() {

    }

    public static void configure(AppContext context) {
        path("bookings", () -> {
            post("create-best-matching", ctx -> {
                final var request = CreateBookingRequest.validated(ctx).getOrThrow(RouteCommons::createBadRequestException);
                final var response = context.bookingsController().bookNextAvailableRoom(request);
                ctx.status(201).json(response);
            });
            get(ctx -> {
                final var date = ctx.queryParamAsClass("date", LocalDate.class).getOrThrow(RouteCommons::createBadRequestException);
                final var response = context.bookingsController().getAllBookings(date);
                ctx.status(200).json(response);
            });
            path("{id}", () -> {
                get(ctx -> {
                    final var id = ctx.pathParamAsClass("id", UUID.class).getOrThrow(RouteCommons::createBadRequestException);
                    final var response = context.bookingsController().getBooking(id);
                    ctx.status(200).json(response);
                });
                delete(ctx -> {
                    final var id = ctx.pathParamAsClass("id", UUID.class).getOrThrow(RouteCommons::createBadRequestException);
                    context.bookingsController().deleteBooking(id);
                    ctx.status(200);
                });
            });
        });
    }
}
