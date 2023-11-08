package com.kntronov.makespace.testing;

import java.util.ArrayList;
import java.util.List;

/**
 * This class should be used to "capture" method invocation arguments in mocked dependencies.
 *
 * @param <T>
 */
public class Captor<T> {

    private final List<T> captured = new ArrayList<>();

    public void capture(T value) {
        captured.add(value);
    }

    public List<T> getAll() {
        return captured;
    }

    public T getLast() {
        return captured.getLast();
    }
}
