package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.features.bazaar.*;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;

import java.util.ArrayList;
import java.util.List;

public class SkyblockBazaar implements Bazaar {

    private final Escrow escrow;
    private final List<BazaarCategory> categories;

    public SkyblockBazaar() {
        this.escrow = null;
        this.categories = new ArrayList<>();

        // todo: add categories
    }

    @Override
    public Escrow getEscrow() {
        return this.escrow;
    }

    @Override
    public List<BazaarCategory> getCategories() {
        return this.categories;
    }

    @Override
    public BazaarItemData getItemData(BazaarItem item) throws BazaarItemNotFoundException {
        return null;
    }

    @Override
    public BazaarItemData getSubItemData(BazaarSubItem item) throws BazaarItemNotFoundException {
        return null;
    }
}
