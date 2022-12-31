package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MiningMinionType;
import com.skyblock.skyblock.enums.MinionType;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Function;

public class MinionHandler {

    private final HashMap<UUID, List<MinionSerializable>> minions;

    public static ItemStack MINION_INVENTORY_PICKUP_MINION = new ItemBuilder(ChatColor.GREEN + "Pickup Minion", Material.BEDROCK).addLore(ChatColor.YELLOW + "Click to pickup!").toItemStack();
    public static ItemStack MINION_INVENTORY_COLLECT_ALL = new ItemBuilder(ChatColor.GREEN + "Collect All", Material.CHEST).addLore(ChatColor.YELLOW + "Click to collect all items!").toItemStack();
    public static ItemStack MINION_INVENTORY_IDEAL_LAYOUT = new ItemBuilder(ChatColor.GREEN + "Ideal Layout", Material.REDSTONE_TORCH_ON).addLore(Util.buildLore("&7View the most effecient spot for\n&7this minion to be placed in.")).toItemStack();
    public static ItemStack MINION_INVENTORY_UPGRADE_SKIN_SLOT = new ItemBuilder(ChatColor.GREEN + "Minion Skin Slot", Material.STAINED_GLASS_PANE, (short) 5).addLore(Util.buildLore("You can insert a Minion Skin\nhere to change the appearance\nof your minion.", '7')).toItemStack();
    public static ItemStack MINION_INVENTORY_UPGRADE_FUEL_SLOT = new ItemBuilder(ChatColor.GREEN + "Fuel", Material.STAINED_GLASS_PANE, (short) 4).addLore(Util.buildLore("Increase the speed of your\nminion by adding minion fuel\nitems here.\n\n&cNote: &7You can't take\nfuel back out after you\nplace it here!", '7')).toItemStack();
    public static ItemStack MINION_INVENTORY_UPGRADE_AUTOMATED_SHIPPING_SLOT = new ItemBuilder(ChatColor.GREEN + "Automated Shipping", Material.STAINED_GLASS_PANE, (short) 11).addLore(Util.buildLore("Add a &bBudget Hopper&7,\n&bEnchanted Hopper&7 or a\n&bPerfect Hopper&7 here to make\nyour minion automatically sell\ngenerated items after its\ninventory is full.", '7')).toItemStack();
    public static ItemStack MINION_INVENTORY_UPGRADE_SLOT = new ItemBuilder(ChatColor.GREEN + "Upgrade Slot", Material.STAINED_GLASS_PANE, (short) 4).addLore(Util.buildLore("You can improve your minion by\nadding a minion upgrade item\nhere.", '7')).toItemStack();

    public static Function<MinionBase, ItemStack> createMinionPreview = (minion) -> {
        ItemStack stack = new ItemBuilder(
                ChatColor.BLUE +
                        WordUtils.capitalize(minion.getType().name().toLowerCase().replace("_", " ")) +
                        " Minion " +
                        Util.toRoman(minion.getLevel()),
                Material.SKULL_ITEM
        ).toItemStack();

        stack = Util.idToSkull(stack, minion.getHead().apply(minion.getLevel()));

        ItemMeta meta = stack.getItemMeta();

        meta.setLore(
                Arrays.asList(
                    Util.buildLore(
                            "Place this minion and it will\nstart generating and " +
                                    MinionTypeAdjective.valueOf(minion.getType().getDeclaringClass().getSimpleName().toUpperCase().replace("MINIONTYPE", "")) +
                                    "\n" +
                                    minion.getType().name().toLowerCase().replace("_", " ") + "!" +
                                    " Requires an open\narea to place " + minion.getType().name().toLowerCase().replace("_", " ") + ".\n" +
                                    "Minions also work when you are\noffline!" +
                                    "\n\nTime Between Action: &a" + minion.getTimeBetweenActions() + "s\n" +
                                    "Max Storage: &e" + minion.getMaxStorage() + "\n" +
                                    "Resources Generated: &b" + minion.getResourcesGenerated(),
                            '7'
                    )
                )
        );

        stack.setItemMeta(meta);

        return stack;
    };

    @Getter
    @AllArgsConstructor
    public enum MinionTypeAdjective {
        MINING("mining");

        private final String value;
    }

    @Data
    @SerializableAs(value = "Minion")
    public static class MinionSerializable implements ConfigurationSerializable {

        private final MinionBase base;
        private final MinionType<?> type;
        private final Location location;
        private final UUID owner;
        private final UUID uuid;
        private final int level;

        @Override
        public Map<String, Object> serialize() {
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();

            result.put("type", this.base.getType().name());
            result.put("location", this.location);
            result.put("owner", this.owner.toString());
            result.put("uuid", this.uuid.toString());
            result.put("level", this.level);
            result.put("items", base.getInventory());

            return result;
        }

        public static MinionSerializable deserialize(Map<String, Object> args) {
            MinionBase base;

            MinionType<?> type;
            Location location;
            UUID owner;
            UUID uuid;
            int level;

            if (args.containsKey("type")) {
                type = MinionType.getEnumValue((String) args.get("type"));
            } else {
                throw new IllegalArgumentException("Could not find serializable class MinionType<?> in serialized data");
            }

            if (args.containsKey("location")) {
                location = (Location) args.get("location");
            } else {
                throw new IllegalArgumentException("Could not find serializable class Location in serialized data");
            }

            if (args.containsKey("owner")) {
                owner = UUID.fromString((String) args.get("owner"));
            } else {
                throw new IllegalArgumentException("Could not find serializable class UUID in serialized data");
            }

            if (args.containsKey("uuid")) {
                uuid = UUID.fromString((String) args.get("uuid"));
            } else {
                throw new IllegalArgumentException("Could not find serializable class UUID in serialized data");
            }

            if (args.containsKey("level")) {
                level = (int) args.get("level");
            } else {
                throw new IllegalArgumentException("Could not find level in serialized data");
            }

            if (type instanceof MiningMinionType) base = new MiningMinion((MiningMinionType) type, uuid);
            else base = null;

            if (args.containsKey("items")) {
                assert base != null;
                base.setInventory((List<ItemStack>) args.get("items"));
            } else {
                throw new IllegalArgumentException("Could not find level in serialized data");
            }

            return new MinionSerializable(base, type, location, owner, uuid, level);
        }

    }

    public MinionHandler() {
        this.minions = new HashMap<>();
    }

    public void reloadPlayer(SkyblockPlayer player, boolean failed) {
        this.minions.put(player.getBukkitPlayer().getUniqueId(), new ArrayList<>());

        for (MinionSerializable minion : (List<MinionSerializable>) player.getValue("island.minions")) {
            boolean found = false;

            String worldName = IslandManager.ISLAND_PREFIX + player.getBukkitPlayer().getUniqueId().toString();

            if (!failed) Skyblock.getPlugin().sendMessage("Attempting to load player island &8" + worldName + "&f...");
            else Skyblock.getPlugin().sendMessage("&cFailed to load player island &8" + worldName + "&c. Retrying...");

            World world = Bukkit.createWorld(new WorldCreator(worldName));

            if (world == null) throw new IllegalArgumentException("Minion World is null (" + worldName + ")");

            try {
                Chunk chunk = minion.getLocation().getChunk();

                if (!chunk.isLoaded()) chunk.load();
            } catch (NullPointerException ex) {
                if (!failed) {
                    reloadPlayer(player, true);
                } else {
                    Skyblock.getPlugin().sendMessage("&cFailed to load player island &8" + worldName + "&c.");
                }
                return;
            }

            for (ArmorStand stand : minion.getLocation().getWorld().getEntitiesByClass(ArmorStand.class)) {
                if (stand.hasMetadata("minion") && stand.getMetadata("minion_id").get(0).asString().equals(minion.getUuid().toString())) {
                    found = true;
                    break;
                }
            }

            if (found) {
                for (ArmorStand stand : minion.getLocation().getWorld().getEntitiesByClass(ArmorStand.class)) {
                    if (stand.hasMetadata("minion")) {
                        if (stand.getMetadata("minion_id").get(0).asString().equals(minion.getUuid().toString())) {
                            stand.remove();
                            break;
                        }
                    }
                }
            }

            minion.getBase().spawn(player, minion.getLocation(), minion.getLevel());
        }
    }

    public void initializeMinion(SkyblockPlayer player, MinionBase minion, Location location) {
        if (!this.minions.containsKey(player.getBukkitPlayer().getUniqueId())) {
            this.minions.put(player.getBukkitPlayer().getUniqueId(), new ArrayList<>());
        }

        MinionSerializable serialize = new MinionSerializable(minion, minion.getType(), location, player.getBukkitPlayer().getUniqueId(), minion.getUuid(), minion.getLevel());

        this.minions.get(player.getBukkitPlayer().getUniqueId()).add(serialize);
        player.getMinions().add(serialize);
    }

    public void deleteAll(UUID uuid) {
        for (MinionSerializable minion : this.minions.get(uuid)) {
            minion.getBase().getMinion().remove();
            minion.getBase().getText().remove();

            SkyblockPlayer player = SkyblockPlayer.getPlayer(uuid);

            List<MinionSerializable> minions = (List<MinionSerializable>) player.getValue("island.minions");

            if (minions == null) minions = new ArrayList<>();

            boolean found = false;
            int i = 0;

            for (MinionSerializable minionSerializable : minions) {
                if (minionSerializable.getUuid().equals(minion.getUuid())) {
                    found = true;
                    break;
                }

                i++;
            }

            if (!found) minions.add(minion);
            else minions.set(i, minion);

            player.setValue("island.minions", minions);
        }
    }

    public MinionBase getMinion(UUID uuid) {
        for (UUID uuid1 : this.minions.keySet()) {
            for (MinionSerializable minion : this.minions.get(uuid1)) {
                if (minion.getUuid().equals(uuid)) return minion.getBase();
            }
        }

        return null;
    }

    public HashMap<UUID, List<MinionSerializable>> getMinions() {
        return minions;
    }
}
