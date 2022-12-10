package com.skyblock.skyblock.utilities;

import lombok.experimental.UtilityClass;

/**
 * Copyright Screaming Sandals 2022
 * Found on GitHub at ScreamingSandals/ScreamingLib
 * @author ScreamingSandals
 */

@UtilityClass
public class SkyblockMath {

    public int square(int n) {
        return n * n;
    }

    public double square(double n) {
        return n * n;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        final long factor = (long) Math.pow(10, places);

        return (double) Math.round(value * factor) / factor;
    }

    public float round(float value, int places) {
        return (float) round((double) value, places);
    }

    public float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public boolean isBetween(int n, int min, int max) {
        return n >= min && n <= max;
    }

    public boolean isBetween(double n, double min, double max) {
        return n >= min && n <= max;
    }

    public boolean isBetween(float n, float min, float max) {
        return n >= min && n <= max;
    }

    public int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }

    public double mod(final double a, final double b) {
        return (a % b + b) % b;
    }

}
