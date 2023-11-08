package com.kntronov.makespace.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Result Test")
class ResultTest {

    @Test
    @DisplayName("when successful result is mapped should create new success with mapped value")
    void mapSuccessTest() {
        final var success = Result.pure(42);
        final var result = success.map(x -> x * 2);
        final var expected = Result.pure(84);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("when failed result is mapped should create new failure with mapped value")
    void mapFailureTest() {
        final var error = new RuntimeException("oops");
        final Result<Integer> failure = Result.fail(error);
        final var result = failure.map(x -> x * 2);
        final var expected = Result.fail(error);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("when successful result is flat mapped should create new success with mapped value")
    void flatMapSuccessTest() {
        final var success = Result.pure(42);
        final var result = success.flatMap(x -> Result.pure(x * 2));
        final var expected = Result.pure(84);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("when failed result is flat mapped should create new failure with mapped value")
    void flatMapFailureTest() {
        final var error = new RuntimeException("oops");
        final Result<Integer> failure = Result.fail(error);
        final var result = failure.map(x -> Result.pure(x * 2));
        final var expected = Result.fail(error);

        assertThat(result).isEqualTo(expected);
    }
}
