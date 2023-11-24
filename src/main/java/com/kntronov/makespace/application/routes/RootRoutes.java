package com.kntronov.makespace.application.routes;

import com.kntronov.makespace.application.AppContext;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * A wrapper over the Javalin DSL that defines roots and their handlers.
 */
public class RootRoutes {

    private RootRoutes() {
    }

    public static void configure(AppContext context) {
        path("api", () -> {
            BookingRoutes.configure(context);
            RoomRoutes.configure(context);
        });
    }
}
