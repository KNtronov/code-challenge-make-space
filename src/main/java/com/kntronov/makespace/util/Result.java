package com.kntronov.makespace.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * Monad-like data structure returning either the value on success or throwable error on failure.
 */
public sealed interface Result<T> {

    /**
     * Create a new instance of successful result.
     *
     * @param value success value
     * @param <T>   success value type
     * @return successful result
     */
    static <T> Result<T> pure(T value) {
        return new Success<>(value);
    }

    /**
     * Create a new instance of failed result.
     *
     * @param error error value
     * @param <T>   success value type
     * @return failed result
     */
    static <T> Result<T> fail(Throwable error) {
        return new Failure<>(error);
    }

    /**
     * If successful return a new successful result with a remapped type, else return failure.
     *
     * @param mapper mapping function
     * @param <F>    new value type
     * @return new success or failure
     */
    <F> Result<F> map(Function<T, F> mapper);

    /**
     * If successful return a new successful result with a remapped type, else return failure.
     *
     * @param mapper mapping function
     * @param <F>    new value type
     * @return new success or failure
     */
    <F> Result<F> flatMap(Function<T, Result<F>> mapper);

    record Success<T>(T value) implements Result<T> {
        public Success {
            Objects.requireNonNull(value);
        }

        @Override
        public <F> Result<F> map(Function<T, F> mapper) {
            return new Success<>(mapper.apply(value));
        }

        @Override
        public <F> Result<F> flatMap(Function<T, Result<F>> mapper) {
            return mapper.apply(value);
        }
    }

    record Failure<T>(Throwable error) implements Result<T> {
        public Failure {
            Objects.requireNonNull(error);
        }

        @Override
        public <F> Result<F> map(Function<T, F> mapper) {
            return new Failure<>(error);
        }

        @Override
        public <F> Result<F> flatMap(Function<T, Result<F>> mapper) {
            return new Failure<>(error);
        }
    }
}
