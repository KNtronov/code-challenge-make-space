package com.kntronov.makespace.testing;

import com.kntronov.makespace.util.Result;

import java.util.function.Consumer;

public class ResultTesting {
    private ResultTesting() {
    }

    public static <T> void expectSuccess(Result<T> result, Consumer<T> continuation) {
        switch (result) {
            case Result.Success<T> success -> continuation.accept(success.value());
            case Result.Failure<T> failure ->
                    throw new AssertionError("expected success, was failure: " + failure.error().getMessage());
        }
    }

    public static <T> void expectFailure(Result<T> result, Consumer<Throwable> continuation) {
        switch (result) {
            case Result.Success<T> success -> throw new AssertionError("expected failure, was success");
            case Result.Failure<T> failure -> continuation.accept(failure.error());
        }
    }
}
