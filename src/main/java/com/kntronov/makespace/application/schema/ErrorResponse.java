package com.kntronov.makespace.application.schema;

public record ErrorResponse(
        int statusCode,
        String error,
        String message
) {
}
