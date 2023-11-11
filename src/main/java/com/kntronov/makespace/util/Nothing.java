package com.kntronov.makespace.util;

/**
 * A value type that represents "Nothing" or "Unit" to be used in generics typed function results instead of
 * non-instantiable Void type.
 */
public final class Nothing {

    private static final Nothing instance = new Nothing();

    private Nothing() {
    }

    public static Nothing get() {
        return instance;
    }
}
