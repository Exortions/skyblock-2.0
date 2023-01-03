package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MinionType;
import com.skyblock.skyblock.features.crafting.SkyblockCraftingRecipe;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionItemType;
import com.skyblock.skyblock.features.minions.items.storages.Storage;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Getter
public abstract class MinionBase {
    protected final UUID uuid;
    protected final String name;
    protected int level;
    protected final Color leatherArmorColor;
    protected final Material material; //arraylist of blocks here? Melon minions have a weird layout

    protected ArmorStand minion;
    protected ArmorStand text;

    protected int resourcesGenerated;
    protected int maxStorage;
    protected int actionRadius;

    protected Inventory gui;

    public List<ItemStack> inventory;

    protected final ArrayList<MinionItemType> minionItemSlots;
    public MinionItem[] minionItems; 
    public int additionalActionRadius = 0; // Modified by Upgrade
    public int fuelAmount = 1;
    public long fuelAddedTime = 0; // minutes

    protected Skyblock plugin;

    public MinionBase(UUID uuid,
                      String name,
                      String adjective,
                      Color leatherArmorColor,
                      Material material) {

        plugin = Skyblock.getPlugin();
        this.minion = null;
        this.level = 1;
        
        this.uuid = uuid;
        this.name = name;
        this.leatherArmorColor = leatherArmorColor;
        this.material = material;

        this.actionRadius = 2;
        this.resourcesGenerated = 0;

        this.inventory = new ArrayList<>();

        minionItemSlots = new ArrayList<>(Arrays.asList(MinionItemType.SKIN,
                                                        MinionItemType.FUEL,
                                                        MinionItemType.SHIPPING,
                                                        MinionItemType.UPGRADE,
                                                        MinionItemType.UPGRADE,
                                                        MinionItemType.STORAGE));
        minionItems = new MinionItem[minionItemSlots.size()];
    }
    
    public abstract SkyblockCraftingRecipe getRecipe(int level);
    public abstract ItemStack getHand(int level);
    public abstract String getHead(int level);
    public abstract int getActionDelay(int level);
    public abstract int getMaxStorage(int level);
    public abstract ArrayList<ItemStack> calculateDrops(int level);
    public abstract int getSlotLevelRequirement(int level);
    
    protected abstract void tick(SkyblockPlayer player, Location location);

    public void spawn(SkyblockPlayer player, Location location, int level) {
        if (!player.isOnIsland()) return;

        this.plugin.getMinionHandler().initializeMinion(player, this, location);

        if (this.minion != null) this.minion.remove();

        this.level = level;

        this.resourcesGenerated = 0;

        this.text = location.getWorld().spawn(location.clone().add(0, 1, 0), ArmorStand.class);
        this.text.setCustomName("");
        this.text.setCustomNameVisible(false);
        this.text.setGravity(false);
        this.text.setVisible(false);
        this.text.setSmall(true);
        this.text.setMarker(true);

        this.minion = location.getWorld().spawn(location, ArmorStand.class);
        this.minion.setCustomName("");
        this.minion.setCustomNameVisible(false);
        this.minion.setGravity(false);
        this.minion.setVisible(true);
        this.minion.setSmall(true);
        this.minion.setArms(true);
        this.minion.setBasePlate(false);
        this.minion.setCanPickupItems(false);

        ItemStack head = Util.idToSkull(new ItemBuilder("", Material.SKULL_ITEM, 1, (short) 3).toItemStack(), getHead(this.level));
        this.minion.setHelmet(head);

        ItemStack hand = this.getHand(level);
        this.minion.setItemInHand(hand);

        this.minion.setChestplate(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_CHESTPLATE, 1).toItemStack(), this.leatherArmorColor));
        this.minion.setLeggings(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_LEGGINGS, 1).toItemStack(), this.leatherArmorColor));
        this.minion.setBoots(Util.colorLeatherArmor(new ItemBuilder("", Material.LEATHER_BOOTS, 1).toItemStack(), this.leatherArmorColor));

        this.minion.setMetadata("minion", new FixedMetadataValue(this.plugin, true));
        this.minion.setMetadata("minion_id", new FixedMetadataValue(this.plugin, this.uuid.toString()));

        this.text.setMetadata("minion", new FixedMetadataValue(this.plugin, true));
        this.text.setMetadata("minion_id", new FixedMetadataValue(this.plugin, this.uuid.toString()));

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (minion == null || minion.isDead()) {
                    cancel();
                    return;
                }

                int ticksBetweenActions = getTimeBetweenActions(level) * 20;

                if (i >= ticksBetweenActions) {
                    i = 0;

                    tick(player, location);
                } else {
                    i++;
                }
            }
        }.runTaskTimer(Skyblock.getPlugin(Skyblock.class), 0, 1);
    }

    public void pickup(SkyblockPlayer player, Location location) {
        List<MinionHandler.MinionSerializable> minions = new ArrayList<>();

        for (MinionHandler.MinionSerializable minion : player.getMinions()) {
            if (!minion.getBase().getUuid().equals(getUuid())) minions.add(minion);
        }

        player.setMinions(minions);

        this.minion.remove();
        this.text.remove();

        player.getBukkitPlayer().getInventory().addItem(Skyblock.getPlugin().getItemHandler().getItem(name + "_GENERATOR_" + level + ".json"));
        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 2);
        collectAll(player);

        player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You picked up a minion! You currently have %s out of a maximum of %s minions placed.");

        for (MinionItem item : minionItems) {
            if (item != null) player.getBukkitPlayer().getInventory().addItem(item.getItem());
        }

        Util.delay(() -> {
            player.getBukkitPlayer().closeInventory();
        }, 1);
    }

    public void collect(SkyblockPlayer player, int inventoryIndex) {

        if (player.getBukkitPlayer().getInventory().firstEmpty() == -1) {
            player.getBukkitPlayer().sendMessage(ChatColor.RED + "Your inventory does not have enough free space to add all items!");
            return;
        }

        ItemStack toCollect = this.inventory.get(inventoryIndex);

        if (!Util.notNull(toCollect)) return;

        player.getBukkitPlayer().getInventory().addItem(Util.toSkyblockItem(toCollect));

        Item item = player.getBukkitPlayer().getWorld().dropItem(minion.getLocation(), Util.toSkyblockItem(toCollect));
        item.setPickupDelay(Integer.MAX_VALUE);

        Bukkit.getPluginManager().callEvent(new PlayerPickupItemEvent(player.getBukkitPlayer(), item, 0));

        Util.delay(item::remove, 1);

        this.inventory.remove(inventoryIndex);

        player.getBukkitPlayer().updateInventory();
    }

    public void collect(SkyblockPlayer player) {
        ArrayList<ItemStack> drops = calculateDrops(this.level);
        for (MinionItem i : this.minionItems) {
            //if (i != null) drops = i.onBlockCollect(this, drops); TODO FIX OR MOVE INTO calulateDrops
        }
        Inventory inventory = Bukkit.createInventory(null, 54);

        this.inventory.forEach((stack) -> { if (stack != null) inventory.addItem(stack); });

        int additionalStorageSlots = 0;
        Storage storage = (Storage) this.minionItems[this.getItemSlots(MinionItemType.STORAGE).get(0)];
        if (storage != null) additionalStorageSlots = storage.capacity;


        for (ItemStack drop : drops) {
            inventory.addItem(drop).values();
            this.resourcesGenerated += drop.getAmount();
        }

        List<ItemStack> newInventory = new ArrayList<>();
        for (int i = 0; i < Math.floor((this.maxStorage + additionalStorageSlots) / 64F); ++i) {
            if (inventory.getItem(i) != null) newInventory.add(inventory.getItem(i));
        }

        this.inventory = newInventory;
		
        if (newInventory.stream().filter(stack -> stack.getType() != Material.AIR).count() == this.maxStorage) {
            this.text.setCustomName(ChatColor.RED + "My storage is full! :(");
            this.text.setCustomNameVisible(true);
            return;
        }

        this.text.setCustomNameVisible(false);
    }

    public void upgrade(SkyblockPlayer player, int level, String item, int amount) {
        this.level = level;
        player.getBukkitPlayer().closeInventory();
        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 2);

        player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You have upgraded your " + name + " to tier " + Util.toRoman(level));

        List<MinionHandler.MinionSerializable> minions = new ArrayList<>();

        for (MinionHandler.MinionSerializable minion : player.getMinions()) {
            if (!minion.getUuid().equals(uuid)) minions.add(minion);
        }

        //minions.add(new MinionHandler.MinionSerializable(this, type, minion.getLocation(), player.getBukkitPlayer().getUniqueId(), uuid, level)); //TODO DO SOMETHING ABOUT THIS

        player.setMinions(minions);

        int amountRemoved = 0;
        for (ItemStack itemInv : player.getBukkitPlayer().getInventory().getContents()) {
            if (itemInv == null) continue;
            if (!itemInv.hasItemMeta()) continue;
            if (!itemInv.getItemMeta().hasDisplayName()) continue;
            if (!itemInv.getItemMeta().getDisplayName().equals(item)) continue;

            while (amountRemoved != amount && itemInv.getAmount() != 0) {
                amountRemoved += 1;
                if (itemInv.getAmount() != 1) itemInv.setAmount(itemInv.getAmount() - 1);
                else {
                    player.getBukkitPlayer().getInventory().remove(itemInv);
                    break;
                }
            }

            if (amountRemoved == amount) break;
        }
    }

    public Material getMaterial() {
        return getMaterial();
    }

    public int getNextMaxStorage() {
        return getMaxStorage(this.level + 1);
    }

    protected void showInventory(SkyblockPlayer player) {
        this.gui = Bukkit.createInventory(null, 54, StringUtils.capitalize(name.toLowerCase()) + " Minion " + Util.toRoman(this.level));

        Util.fillEmpty(this.gui);

        this.gui.setItem(4, MinionHandler.createMinionPreview.apply(this));
        this.gui.setItem(5, MinionHandler.createNextTierItem(this));

        this.gui.setItem(3, MinionHandler.MINION_INVENTORY_IDEAL_LAYOUT);
        this.gui.setItem(50, MinionHandler.createQuickUpgrade(this, player.getBukkitPlayer()));
        this.gui.setItem(53, MinionHandler.MINION_INVENTORY_PICKUP_MINION);
        
        MinionItem skin = this.minionItems[getItemSlots(MinionItemType.SKIN).get(0)];
        MinionItem fuel = this.minionItems[getItemSlots(MinionItemType.FUEL).get(0)];
        MinionItem shipping = this.minionItems[getItemSlots(MinionItemType.SHIPPING).get(0)];
        MinionItem up1 = this.minionItems[getItemSlots(MinionItemType.UPGRADE).get(0)];
        MinionItem up2 = this.minionItems[getItemSlots(MinionItemType.UPGRADE).get(1)];
        this.gui.setItem(10, skin == null ? MinionHandler.MINION_INVENTORY_UPGRADE_SKIN_SLOT : skin.getItem());
        this.gui.setItem(19, fuel == null ? MinionHandler.MINION_INVENTORY_UPGRADE_FUEL_SLOT : fuel.getItem());
        this.gui.setItem(28, shipping == null ? MinionHandler.MINION_INVENTORY_UPGRADE_AUTOMATED_SHIPPING_SLOT : shipping.getItem());
        this.gui.setItem(37, up1 == null ? MinionHandler.MINION_INVENTORY_UPGRADE_SLOT : up1.getItem());
        this.gui.setItem(46, up2 == null ?  MinionHandler.MINION_INVENTORY_UPGRADE_SLOT : up2.getItem());

        this.gui.setItem(48, MinionHandler.MINION_INVENTORY_COLLECT_ALL);

        int slot = 21;
        for (int i = 0; i < 15; i++) {
            if (i < Math.floor(this.maxStorage / 64F) ) {
	        if (i < this.inventory.size() && this.inventory.get(i).getType() != Material.AIR) {
                    NBTItem item = new NBTItem(this.inventory.get(i));
                    item.setInteger("slot", i);
                    this.gui.setItem(slot, item.getItem());
	        }
                else
                    this.gui.setItem(slot, new ItemStack(Material.AIR));

            } else {
                this.gui.setItem(slot, new ItemBuilder(ChatColor.YELLOW + "Storage unlocked at tier " + Util.toRoman(getSlotLevelRequirement(i)), Material.STAINED_GLASS_PANE).toItemStack());
            }

            if (slot == 25) {
                slot = 30;
            } else if (slot == 34) {
                slot = 39;
            } else {
                ++slot;
            }
        }
        player.getBukkitPlayer().openInventory(this.gui);
    }

    public void collectAll(SkyblockPlayer player) {
        for (int i = 0; i < gui.getSize(); i++) collect(player, i);
    }


    public void addItem(MinionItem item) {
        ArrayList<Integer> slots = getItemSlots(item.getType());
        if (slots.size() > 0) {
            for (int slot : slots) {
                if (minionItems[slot] == null) {
                    minionItems[slot] = item;
                    minionItems[slot].onEquip(this);
                }
            }
        }
    }

    public void removeItem(int slot) {
        minionItems[slot].onUnEquip(this);
        minionItems[slot] = null;
    }

    public ArrayList<Integer> getItemSlots(MinionItemType type) {
        ArrayList<Integer> slots = new ArrayList<>();
        for (int i = 0; i < minionItemSlots.size(); ++i) {
            if (minionItemSlots.get(i) == type) slots.add(i);
        }
        return slots;
    }
}
