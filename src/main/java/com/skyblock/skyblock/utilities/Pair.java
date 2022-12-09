package com.skyblock.skyblock.utilities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Copyright Screaming Sandals 2022
 * Found on GitHub at ScreamingSandals/ScreamingLib
 * @author ScreamingSandals
 */

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Pair<F, S> {

    private final F first;
    private final S second;

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    public static <F, S> Pair<F, S> empty() {
        return new Pair<>(null, null);
    }

    public boolean bothPresent() {
        return this.first != null && this.second != null;
    }

    public boolean isPresent() {
        return this.first != null || this.second != null;
    }

    public boolean isEmpty() {
        return this.first == null && this.second == null;
    }

    public F first() {
        return this.first;
    }

    public S second() {
        return this.second;
    }

}
