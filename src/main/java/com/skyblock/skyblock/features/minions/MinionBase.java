package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.minions.items.MinionFuel;
import com.skyblock.skyblock.features.minions.items.MinionItem;
import com.skyblock.skyblock.features.minions.items.MinionItemType;
import com.skyblock.skyblock.features.minions.items.storages.Storage;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public abstract class MinionBase {
    protected final UUID uuid;
    protected final String name;
    protected final String adjective;
    protected int level;
    protected final Color leatherArmorColor;
    protected final Material material; // TODO: Make array? (for multiple materials)

    protected ArmorStand minion;
    protected ArmorStand text;

    protected int resourcesGenerated;
    protected int maxStorage;
    protected int actionRadius;

    protected Inventory gui;

    public List<ItemStack> inventory;


    // calculation for additional speed: base speed / (1 + combined speed boost percent)

    protected final ArrayList<MinionItemType> minionItemSlots;
    public MinionItem[] minionItems; 
    public int additionalActionRadius = 0; // Modified by Upgrade
    public int fuelAmount = 0;
    public long fuelAddedTime = 0; // (in minutes)

    protected Skyblock plugin;

    public MinionBase(UUID uuid,
                      String name,
                      String adjective,
                      Color leatherArmorColor,
                      Material material) {

        this.plugin = Skyblock.getPlugin();
        this.minion = null;
        this.level = 1;
        
        this.uuid = uuid;
        this.name = name;
        this.adjective = adjective;
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
    
    public abstract ItemStack getHand(int level);
    public abstract String getHead(int level);
    public abstract int getActionDelay(int level);
    public abstract int getMaxStorage(int level);
    public abstract ArrayList<ItemStack> calculateDrops(int level);
    public abstract int getSlotLevelRequirement(int level);
    protected abstract void tick(SkyblockPlayer player, Location location);

    private float onSleepHook() {
        float delay = getActionDelay(this.level);
        for (MinionItem i : minionItems) {
            if (i != null) delay = i.onSleep(this, delay);
        }

        return delay;
    }

    private void onTickHook() {
        for (MinionItem i : minionItems) {
            if (i != null) i.onTick(this);
        }
    }

    private void postTickHook() {
        for (MinionItem i : minionItems) {
            if (i != null) i.postTick(this);
        }
    }

    private ArrayList<ItemStack> onBlockCollectHook(ArrayList<ItemStack> drops) {
        for (MinionItem i : minionItems) {
            if (i != null) drops = i.onBlockCollect(this, drops);
        }

        return drops;
    }

    private void checkFuelDuration() {
        for (int i = 0; i < minionItems.length; ++i) {
            if (minionItems[i] != null && minionItems[i] instanceof MinionFuel) {
                int duration = ((MinionFuel) minionItems[i]).duration;
                if (duration == -1) continue;
                else if (fuelAddedTime + duration < Math.floor(System.currentTimeMillis() / 60000) && --fuelAmount < 1) {
                    minionItems[i].onUnEquip(this);
                    minionItems[i] = null;
                }
            }
        }
    }

    public void spawn(SkyblockPlayer player, Location location, int level) {
        if (!player.isOnIsland()) return;

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

        this.plugin.getMinionHandler().initializeMinion(player, this, location);

        this.minion.setHeadPose(new EulerAngle(0, Math.toRadians(location.getPitch()), 0));

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (minion == null || minion.isDead()) {
                    cancel();
                    return;
                }

                int ticksBetweenActions = Math.round(onSleepHook() * 20);

                if (i >= ticksBetweenActions) {
                    i = 0;
                    onTickHook();
                    tick(player, location);
                    postTickHook();
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

        int minionsPlaced = ((List<Object>) player.getValue("island.minions")).size();
        int minionSlots = (int) player.getValue("island.minion.slots");

        player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "You picked up a minion! You currently have " + minionsPlaced + " out of a maximum of " + minionSlots + " minions placed.");

        player.getBukkitPlayer().getInventory().addItem(Skyblock.getPlugin().getItemHandler().getItem(name + "_generator_" + level));
        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 2);
        collectAll(player);

        for (MinionItem item : minionItems) {
            if (item != null) player.getBukkitPlayer().getInventory().addItem(item.getItem());
        }

        Util.delay(() -> player.getBukkitPlayer().closeInventory(), 1);
    }

    public void collect(SkyblockPlayer player, int inventoryIndex) {
        if (player.getBukkitPlayer().getInventory().firstEmpty() == -1) {
            player.getBukkitPlayer().sendMessage(ChatColor.RED + "Your inventory does not have enough free space to add all items!");
            return;
        }

        try {
            ItemStack toCollect = this.inventory.get(inventoryIndex);

            if (!Util.notNull(toCollect)) return;

            ItemStack stack = Util.toSkyblockItem(toCollect);
            stack.setAmount(toCollect.getAmount());

            Item item = player.getBukkitPlayer().getWorld().dropItem(minion.getLocation(), stack);
            item.setPickupDelay(Integer.MAX_VALUE);

            Bukkit.getPluginManager().callEvent(new PlayerPickupItemEvent(player.getBukkitPlayer(), item, 0));

            Util.delay(item::remove, 1);

            this.inventory.remove(inventoryIndex);

            player.getBukkitPlayer().updateInventory();
        } catch (IndexOutOfBoundsException ignored) { }
    }

    public void collect(SkyblockPlayer player) {
        ArrayList<ItemStack> drops = calculateDrops(this.level);
        drops = onBlockCollectHook(drops);

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
        for (int i = 0; i < getMaxStorage(level) + additionalStorageSlots; ++i) {
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
        return this.material;
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
        ItemStack fuelItem = null;
        if (fuel != null) {
            fuelItem = fuel.getItem();
            fuelItem.setAmount(fuelAmount);
        }
        MinionItem shipping = this.minionItems[getItemSlots(MinionItemType.SHIPPING).get(0)];
        MinionItem up1 = this.minionItems[getItemSlots(MinionItemType.UPGRADE).get(0)];
        MinionItem up2 = this.minionItems[getItemSlots(MinionItemType.UPGRADE).get(1)];
        this.gui.setItem(10, skin == null ? MinionHandler.MINION_INVENTORY_UPGRADE_SKIN_SLOT : skin.getItem());
        this.gui.setItem(19, fuelItem == null ? MinionHandler.MINION_INVENTORY_UPGRADE_FUEL_SLOT : fuelItem);
        this.gui.setItem(28, shipping == null ? MinionHandler.MINION_INVENTORY_UPGRADE_AUTOMATED_SHIPPING_SLOT : shipping.getItem());
        this.gui.setItem(37, up1 == null ? MinionHandler.MINION_INVENTORY_UPGRADE_SLOT : up1.getItem());
        this.gui.setItem(46, up2 == null ?  MinionHandler.MINION_INVENTORY_UPGRADE_SLOT : up2.getItem());

        this.gui.setItem(48, MinionHandler.MINION_INVENTORY_COLLECT_ALL);

        int slot = 21;
        for (int i = 0; i < 15; i++) {
            if (i < getMaxStorage(level)) {
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

    public static String getHeadVaueFromMinion(String minion, int level) {
        ItemStack item = Skyblock.getPlugin().getItemHandler().getItem(minion.toUpperCase() + "_GENERATOR_" + level);

        if (item == null) return null;
        if (item.getType() != Material.SKULL_ITEM) return null;

        // get game profile from item
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound skullOwner = nbtItem.getCompound("SkullOwner");
        if (skullOwner == null) return null;
        NBTCompound properties = skullOwner.getCompound("Properties");
        if (properties == null) return null;
        NBTCompoundList textures = properties.getCompoundList("textures");
        if (textures == null) return null;
        NBTCompound texture = textures.get(0);
        if (texture == null) return null;

        return texture.getString("Value");

//        Collection<Property> props = profile.getProperties().get("textures");
//        Property prop = props.iterator().next();
//        String value = prop.getValue();
//
//        JSONObject json;
//
//        try {
//            json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(value)));
//        } catch (ParseException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//
//        if (json == null) return null;
//
//        JSONObject textures = (JSONObject) json.get("textures");
//        JSONObject skin = (JSONObject) textures.get("SKIN");
//
//        return (String) skin.get("url");
    }
}

