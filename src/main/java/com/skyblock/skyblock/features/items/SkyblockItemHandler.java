package com.skyblock.skyblock.features.items;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.items.armor.*;
import com.skyblock.skyblock.features.items.bows.*;
import com.skyblock.skyblock.features.items.misc.*;
import com.skyblock.skyblock.features.items.tools.*;
import com.skyblock.skyblock.features.items.weapons.*;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;

import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkyblockItemHandler {

    private final HashMap<String, SkyblockItem> items;
    private final HashMap<String, ArmorSet> sets;

    public SkyblockItemHandler(Skyblock plugin){
        items = new HashMap<>();
        sets = new HashMap<>();

        // Misc
        registerItem(new GrapplingHook(plugin));
        registerItem(new MaddoxBatphone());
        registerItem(new MagicalWaterBucket(plugin));
        registerItem(new WandOfHealing(plugin, "WAND_OF_HEALING", 60));
        registerItem(new WandOfHealing(plugin, "WAND_OF_MENDING", 100));
        registerItem(new WandOfHealing(plugin, "WAND_OF_RESTORATION", 120));
        registerItem(new BiomeStick(plugin, "BIRCH_FOREST_BIOME_STICK", Biome.BIRCH_FOREST));
        registerItem(new BiomeStick(plugin, "DEEP_OCEAN_BIOME_STICK", Biome.DEEP_OCEAN));
        registerItem(new BiomeStick(plugin, "DESERT_BIOME_STICK", Biome.DESERT));
        //registerItem(new BiomeStick(plugin, "END_FOREST_BIOME_STICK", Biome.END));
        registerItem(new BiomeStick(plugin, "FOREST_BIOME_STICK", Biome.FOREST));
        registerItem(new BiomeStick(plugin, "JUNGLE_BIOME_STICK", Biome.JUNGLE));
        registerItem(new BiomeStick(plugin, "MESA_BIOME_STICK", Biome.MESA));
        registerItem(new BiomeStick(plugin, "SAVANNA_BIOME_STICK", Biome.SAVANNA));
        registerItem(new BiomeStick(plugin, "TAIGA_BIOME_STICK", Biome.TAIGA));
        //registerItem(new BiomeStick(plugin, "NETHER_BIOME_STICK", Biome.NETHER));
        registerItem(new BiomeStick(plugin, "ROOFED_FOREST_BIOME_STICK", Biome.ROOFED_FOREST));

        // Tools
        registerItem(new PromisingAxe());
        registerItem(new PromisingPickaxe());
        registerItem(new PromisingShovel());
        registerItem(new SweetAxe());
        registerItem(new EfficientAxe());
        registerItem(new RookieHoe());
        registerItem(new JungleAxe());
        registerItem(new TreeCapitator());

        // Weapons
        registerItem(new AspectOfTheDragons(plugin));
        registerItem(new AspectOfTheEnd(plugin));
        registerItem(new EndStoneSword(plugin));
        registerItem(new RogueSword(plugin));
        registerItem(new AspectOfTheJerry());
        registerItem(new EmberRod());
        registerItem(new EndSword());
        registerItem(new Cleaver());
        registerItem(new GolemSword(plugin));
        registerItem(new UndeadSword());
        registerItem(new RevenantFalchion());
        registerItem(new SpiderSword());
        registerItem(new ZombieSword());
        registerItem(new MidasSword());
        registerItem(new RaiderAxe());
        registerItem(new EmeraldBlade());
        registerItem(new TacticiansSword());
        registerItem(new PigmanSword());
        registerItem(new FrozenScythe());
        registerItem(new ScorpionFoil());
        registerItem(new FlamingSword());
        registerItem(new EdibleMace());
        registerItem(new ShamanSword());
        registerItem(new LeapingSword());
        registerItem(new PoochSword());
        registerItem(new InkWand());

        // Bows
        registerItem(new RunaansBow());
        registerItem(new EndstoneBow());
        registerItem(new WitherBow());
        registerItem(new SavannaBow());
        registerItem(new MosquitoBow());
        registerItem(new HurricaneBow());
        registerItem(new ScorpionBow());
        registerItem(new EnderBow());
        registerItem(new ExplosiveBow());

        // Armor Sets
        registerItem(new StrongDragonArmor());
        registerItem(new SuperiorDragonArmor());
        registerItem(new WiseDragonArmor());
        registerItem(new YoungDragonArmor());
        registerItem(new LapisArmor());
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (Map.Entry<String, SkyblockItem> entry : items.entrySet()) {
            list.add(entry.getValue().getItem());
        }
        return list;
    }

    private void registerItem(SkyblockItem item){
        items.put(item.getInternalName(), item);
    }

    public boolean isRegistered(ItemStack item){
        return getRegistered(item) != null;
    }

    public SkyblockItem getRegistered(ItemStack item){
        if (item == null)  return null;
        if (item.getItemMeta() == null) return null;

        if (item.getItemMeta().hasDisplayName()) {
            NBTItem nbtItem = new NBTItem(item);

            return items.get(nbtItem.getString("skyblockId"));
        }

        return null;
    }

    public ArmorSet getRegisteredSet(String name) {
        return sets.get(name);
    }

    public SkyblockItem getRegistered(String s){
        return items.get(s);
    }

    private void registerItem(ArmorSet set){
        sets.put(set.getId(), set);
    }

    public boolean isRegistered(ItemStack[] set){
        return getRegistered(set) != null;
    }

    public ArmorSet getRegistered(ItemStack[] set){
        ItemStack helmet = set[3];
        ItemStack chest = set[2];
        ItemStack legs = set[1];
        ItemStack boots = set[0];

        for (ArmorSet armorSet : sets.values()) {
            if (getID(helmet).equals(getID(armorSet.getHelmet())) &&
                getID(chest).equals(getID(armorSet.getChest())) &&
                getID(legs).equals(getID(armorSet.getLegs())) &&
                getID(boots).equals(getID(armorSet.getBoots()))) {
                return armorSet;
            }
        }

        return null;
    }

    private String getID(ItemStack stack) {
        if (!Util.notNull(stack)) return "";
        return new NBTItem(stack).getString("skyblockId");
    }
}
