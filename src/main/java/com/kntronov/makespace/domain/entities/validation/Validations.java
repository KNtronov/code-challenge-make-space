package com.kntronov.makespace.domain.entities.validation;

public class Validations {

    private Validations() {

    }

    public static void validateNotNull(String fieldName, Object o) {
        if (o == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }

    public static void validateNotBlank(String fieldName, String s) {
        if (s.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    public static void validateGreaterThanZero(String fieldName, int i) {
        if (i <= 0) {
            throw new IllegalArgumentException(fieldName + " must not be lesser or equal to zero");
        }
    }
}
