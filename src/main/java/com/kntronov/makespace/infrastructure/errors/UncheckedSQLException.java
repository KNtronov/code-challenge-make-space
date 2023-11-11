package com.kntronov.makespace.infrastructure.errors;

/**
 * Unchecked exception to be used instead of the checked SQLException or handling applicative runtime SQL errors.
 */
public class UncheckedSQLException extends RuntimeException {

    public UncheckedSQLException(Throwable cause) {
        super(cause);
    }
}
