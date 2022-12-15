package com.skyblock.skyblock.features.bazaar.impl;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.bazaar.*;
import com.skyblock.skyblock.features.bazaar.escrow.Escrow;
import com.skyblock.skyblock.features.bazaar.impl.escrow.SkyblockEscrow;
import com.skyblock.skyblock.utilities.Pair;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SkyblockBazaar implements Bazaar {

    private final Escrow escrow;
    private final List<BazaarCategory> categories;

    private final List<BazaarSubItem> rawItems;

    private final YamlConfiguration config;
    private final File itemsFile;
    private final File file;

    public SkyblockBazaar() throws BazaarIOException, BazaarItemNotFoundException {
        this.escrow = new SkyblockEscrow(this);

        this.itemsFile = new File(Skyblock.getPlugin().getDataFolder(), Bazaar.ITEMS_PATH);
        this.file = new File(Skyblock.getPlugin().getDataFolder(), Bazaar.FILE_NAME);

        try {
            if (!this.itemsFile.exists()) {
                Skyblock.getPlugin().sendMessage("&cCould not find " + Bazaar.ITEMS_PATH + ", unable to start Bazaar.");
                Skyblock.getPlugin().getServer().getPluginManager().disablePlugin(Skyblock.getPlugin());
                throw new BazaarIOException("Could not find " + Bazaar.ITEMS_PATH + ", unable to start Bazaar.");
            }

            if (!this.file.exists())
                this.file.getParentFile().mkdirs();
        } catch (BazaarIOException ex) {
            throw new BazaarIOException("Failed to create bazaar config files", ex);
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);

        Pair<List<BazaarCategory>, List<BazaarSubItem>> indexed = new SkyblockBazaarConfigIndexer(this, this.itemsFile).index();
//        temp: (fixed by above code line)
//        this.rawItems.add(new SkyblockBazaarSubItem(Skyblock.getPlugin().getItemHandler().getItem("ENCHANTED_PUMPKIN.json"), Rarity.UNCOMMON, 12, new ArrayList<BazaarOffer>() {{
//            add(new SkyblockBazaarOffer(UUID.randomUUID(), 1349, 824.3));
//        }}, new ArrayList<>()));
//
//        this.categories.add(new SkyblockBazaarCategory("Farming", Material.GOLD_HOE, ChatColor.YELLOW, (short) 4, new ArrayList<BazaarItem>() {{
//            add(new SkyblockBazaarItem("Pumpkin", new ArrayList<BazaarSubItem>() {{
//                add(rawItems.get(0));
//            }}, 36));
//        }}));

        this.categories = indexed.getFirst();
        this.rawItems = indexed.getSecond();

        if (!this.file.exists()) {
            try {
                boolean success = this.file.createNewFile();

                if (!success) throw new IOException("File#createNewFile returned false");
            } catch (IOException ex) {
                throw new BazaarIOException("Failed to create " + file.getAbsolutePath() + ", please check your file permissions.", ex);
            }

            this.set("items", new ArrayList<>());

            this.rawItems.forEach(item -> {
                try {
                    String displayName = item.getIcon().getItemMeta().getDisplayName();

                    boolean hasSkyblockNamespace = true;

                    if (displayName == null) {
                        displayName = item.getIcon().getType().name();
                        hasSkyblockNamespace = false;
                    }
                    String name = (hasSkyblockNamespace ? "skyblock;": "minecraft;") + ChatColor.stripColor(displayName).toUpperCase().replace(" ", "_");
                    this.set("items." + name + ".buyPrice", 0.0);
                    this.set("items." + name + ".sellPrice", 0.0);
                    this.set("items." + name + ".orders", item.getOrders());
                    this.set("items." + name + ".offers", item.getOffers());
                    this.set("items." + name + ".buyVolume.amount", 0);
                    this.set("items." + name + ".buyVolume.offers", 0);
                    this.set("items." + name + ".last7dInstantBuyVolume", 0);
                    this.set("items." + name + ".sellVolume.amount", 0);
                    this.set("items." + name + ".sellVolume.orders", 0);
                    this.set("items." + name + ".last7dInstantSellVolume", 0);
                } catch (BazaarIOException ex) {
                    ex.printStackTrace();
                }
            });

            this.categories.forEach(category -> {
                try {
                    this.set("categories." + category.getName() + ".icon", category.getIcon().name());
                    this.set("categories." + category.getName() + ".items", new ArrayList<>());

                    for (BazaarItem item : category.getItems()) {
                        this.set("categories." + category.getName() + ".items." + item.getName(), new ArrayList<>());

                        for (BazaarSubItem subItem : item.getSubItems()) {
                            String displayName = subItem.getIcon().getItemMeta().getDisplayName();

                            boolean hasSkyblockNamespace = true;

                            if (displayName == null) {
                                displayName = subItem.getIcon().getType().name();
                                hasSkyblockNamespace = false;
                            }

                            this.set("categories." + category.getName() + ".items." + item.getName() + ".size", item.getInventorySize());

                            this.set("categories." + category.getName() + ".items." + item.getName() + ".slots." + (hasSkyblockNamespace ? "skyblock;" : "minecraft;") + ChatColor.stripColor(displayName).toUpperCase().replace(" ", "_"), subItem.getSlot());
                        }
                    }
                } catch (BazaarIOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    public YamlConfiguration getBazaarConfig() {
        return this.config;
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
    public List<BazaarSubItem> getRawItems() {
        return rawItems;
    }

    @Override
    public void setCategories(List<BazaarCategory> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
    }

    @Override
    public void setRawItems(List<BazaarSubItem> rawItems) {
        this.rawItems.clear();
        this.rawItems.addAll(rawItems);
    }

    @Override
    public BazaarItemData getItemData(String name) throws BazaarItemNotFoundException {
        AtomicReference<SkyblockBazaarItemData> data = new AtomicReference<>();

        this.config.getConfigurationSection("items").getKeys(false).forEach(key -> {
            if (key.equalsIgnoreCase(name)) {
                data.set(new SkyblockBazaarItemData(
                        this.get("items." + key + ".productAmount", Integer.class),
                        this.get("items." + key + ".buyPrice", Double.class),
                        this.get("items." + key + ".sellPrice", Double.class),
                        new SkyblockBazaarItemData.SkyblockBazaarItemVolume(
                                this.get("items." + key + ".buyVolume.amount", Integer.class),
                                this.get("items." + key + ".buyVolume.offers", Integer.class)
                        ),
                        this.get("items." + key + ".last7dInstantBuyVolume", Integer.class),
                        new SkyblockBazaarItemData.SkyblockBazaarItemVolume(
                                this.get("items." + key + ".sellVolume.amount", Integer.class),
                                this.get("items." + key + ".sellVolume.orders", Integer.class)
                        ),
                        this.get("items." + key + ".last7dInstantSellVolume", Integer.class),
                        this.get("items." + key + ".orders", List.class),
                        this.get("items." + key + ".offers", List.class)
                ));
            }
        });

        if (data.get() == null) throw new BazaarItemNotFoundException("Item " + name + " not found in bazaar config");

        return data.get();
    }

    @Override
    public void set(String path, Object value) throws BazaarIOException{
        try {
            this.config.set(path, value);
            this.config.save(this.file);
        } catch (IOException ex) {
            throw new BazaarIOException("Failed to save bazaar config", ex);
        }
    }

    @Override
    public Object get(String path) {
        return this.config.get(path);
    }

    public <T> T get(String path, Class<T> clazz) {
        return clazz.cast(this.get(path));
    }

}
