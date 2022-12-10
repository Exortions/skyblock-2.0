package com.skyblock.skyblock.utilities;

import java.util.Objects;
import java.util.function.Function;

/**
 * Copyright Screaming Sandals 2022
 * Found on GitHub at ScreamingSandals/ScreamingLib
 * @author ScreamingSandals
 */

public interface TriFunction<T, U, V, R> {

    R apply(T t, U u, V v);

    default <W> TriFunction<T, U, V, W> andThen(Function<? super R, ? extends W> after) {
        Objects.requireNonNull(after);

        return (T t, U u, V v) -> after.apply(apply(t, u, v));
    }

}
