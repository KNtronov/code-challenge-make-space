package com.kntronov.makespace.application;

import com.kntronov.makespace.application.errors.HttpError;
import io.javalin.validation.ValidationError;

import java.util.List;
import java.util.Map;

/**
 * DSL utils for routes.
 */
public class RouteCommons {
    private RouteCommons() {
    }

    public static Exception createBadRequestException(Map<String, ? extends List<ValidationError<Object>>> stringMap) {
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
