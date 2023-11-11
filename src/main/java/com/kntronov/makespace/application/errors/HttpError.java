package com.kntronov.makespace.application.errors;

import java.util.List;

/**
 * HttpError is to be used in server's error handling as a common error response schema.
 */
public sealed interface HttpError {

    int code();

    String errorName();

    String message();

    final class BadRequestException extends RuntimeException implements HttpError {

        private final List<String> errors;

        public BadRequestException(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public int code() {
            return 400;
        }

        @Override
        public String errorName() {
            return "BadRequest";
        }

        @Override
        public String message() {
            if (errors.isEmpty()) {
                return "";
            } else {
                return errors.stream()
                        .reduce((a, b) -> a + "," + b)
                        .orElse(errors.get(0));
            }
        }

    }

    final class InternalServerErrorException extends RuntimeException implements HttpError {
        public InternalServerErrorException() {
            super("something went wrong");
        }

        public InternalServerErrorException(String message) {
            super(message);
        }

        @Override
        public int code() {
            return 500;
        }

        @Override
        public String errorName() {
            return "InternalServerError";
        }

        @Override
        public String message() {
            return getMessage();
        }
    }

    final class NoContentException extends RuntimeException implements HttpError {
        public NoContentException(String message) {
            super(message);
        }

        @Override
        public int code() {
            return 204;
        }

        @Override
        public String errorName() {
            return "NoContent";
        }

        @Override
        public String message() {
            return getMessage();
        }
    }
}
