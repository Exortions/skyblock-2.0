package com.skyblock.skyblock.utilities;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Util {

    public List<String> listOf(String... strings) {
        return Arrays.asList(strings);
    }
}
