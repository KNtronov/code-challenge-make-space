package com.kntronov.makespace.application;

public class AppErrors {
    private AppErrors() {
    }

    public static String badRequestError(String message) {
        return "{\"statusCode\":400,\"error\":\"BadRequest\",\"message\":\"" + message + "\"}";
    }

    public static String internalServerError() {
        return "{\"statusCode\":500,\"error\":\"InternalServerError\",\"message\":\"something went wrong\"}";
    }
}
