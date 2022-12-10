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
public class Triple<F, S, T> {

    private final F first;
    private final S second;
    private final T third;

    public static <F, S, T> Triple<F, S, T> of(F first, S second, T third) {
        return new Triple<>(first, second, third);
    }

    public static <F, S, T> Triple<F, S, T> empty() {
        return new Triple<>(null, null, null);
    }

    public boolean allPresent() {
        return this.first != null && this.second != null && this.third != null;
    }

    public boolean isPresent() {
        return this.first != null || this.second != null || this.third != null;
    }

    public boolean isEmpty() {
        return this.first == null && this.second == null && this.third == null;
    }

    public F first() {
        return this.first;
    }

    public S second() {
        return this.second;
    }

    public T third() {
        return this.third;
    }

}
