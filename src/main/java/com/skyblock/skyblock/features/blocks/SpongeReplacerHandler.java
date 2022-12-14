package com.skyblock.skyblock.features.blocks;

import java.util.ArrayList;
import java.util.List;

public class SpongeReplacerHandler {

    private static final List<SpongeReplacer> replacers = new ArrayList<>();

    public void registerReplacer(SpongeReplacer replacer) {
        replacers.add(replacer);
    }

    public void startGeneration() {
        for (SpongeReplacer replacer : replacers) {
            replacer.generate();
        }
    }

    public void endGeneration() {
        for (SpongeReplacer replacer : replacers) {
            replacer.end();
        }
    }
}
