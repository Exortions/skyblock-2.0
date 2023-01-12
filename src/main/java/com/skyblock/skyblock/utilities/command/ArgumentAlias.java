package com.skyblock.skyblock.utilities.command;

import java.util.HashMap;

public interface ArgumentAlias {

    default HashMap<String, String> getArgumentAliases() {
        return new HashMap<>();
    }

}
