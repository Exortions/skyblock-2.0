package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.features.crafting.SkyblockRecipe;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.pets.PetType;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Getter
public class ItemHandler {
    private final HashMap<String, ItemStack> items = new HashMap<>();
    private final HashMap<ItemStack, String> reversed = new HashMap<>();
    private final Skyblock skyblock;

    public ItemHandler(Skyblock plugin) {
        this.skyblock = plugin;
    }

    public void init() {
        File folder = new File(skyblock.getDataFolder() + File.separator + "items");

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().equals("FANCY_SWORD.json")) continue;

            try {
                JSONParser parser = new JSONParser();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
                Object obj = parser.parse(bufferedReader);

                JSONObject jsonObject =  (JSONObject) obj;
                String itemId = (String) jsonObject.get("itemid");
                String displayName = (String) jsonObject.get("displayname");

                String nbt = (String) jsonObject.get("nbttag");

                long damage = (long) jsonObject.get("damage");

                JSONArray lore = (JSONArray) jsonObject.get("lore");

                ItemStack item = CraftItemStack.asNewCraftStack(Item.d(itemId));
                item.setDurability((short) damage);

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(displayName);

                //noinspection unchecked
                meta.setLore(lore);
                meta.spigot().setUnbreakable(true);

                item.setItemMeta(meta);

                net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
                nms.setTag(MojangsonParser.parse(nbt));

                NBTItem nbtItem = new NBTItem(CraftItemStack.asBukkitCopy(nms));
                nbtItem.setString("skyblockId", file.getName().replace(".json", "").toLowerCase());

                register(file.getName(), nbtItem.getItem());

                if (jsonObject.get("recipe") != null) {
                    JSONObject recipe = (JSONObject) jsonObject.get("recipe");
                    Skyblock.getPlugin(Skyblock.class).getRecipeHandler().getRecipes().add(
                            new SkyblockRecipe(recipe, getItem(file.getName())));
                }

                bufferedReader.close();
            } catch (MojangsonParseException | ParseException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        registerCustomItems();
    }

    public void registerCustomItems() {
        skyblock.sendMessage("Registering custom items...");

        long start = System.currentTimeMillis();

        List<ItemEnchantment> enchantments = new ArrayList<>();

        enchantments.add(new ItemEnchantment(skyblock.getEnchantmentHandler().getEnchantment("sharpness"), 3));

        items.put("fancy_sword", new ItemBase(
                Material.GOLD_SWORD,
                ChatColor.WHITE + "Fancy Sword",
                Reforge.NONE,
                1,
                new ArrayList<>(),
                enchantments,
                true,
                false,
                "",
                new ArrayList<>(),
                "",
                0,
                "",
                "COMMON SWORD",
                "fancy_sword",
                20,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                false
        ).createStack());

        for (Rarity r : Rarity.values()) {
            for (PetType type : PetType.values()) {
                Pet pet = type.newInstance();
                if (pet == null) continue;
                pet.setRarity(r);
                items.put(type.name(), pet.toItemStack());
            }

            if (r.equals(Rarity.LEGENDARY)) break;
        }

        skyblock.sendMessage("Sucessfully Registered custom items [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void register(String id, ItemStack item) {
        items.put(id, parseLore(item));
        reversed.put(parseLore(item), id.replace(".json", ""));
    }

    public ItemStack getItem(String s) {
        return items.get(s);
    }

    private ItemStack parseLore(ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        nbt.setBoolean("skyblockItem", true);
        nbt.setString("name", meta.getDisplayName());
        nbt.setString("reforge", "NO_REFORGE");

        if (item.getEnchantments().size() > 0) {
            nbt.setBoolean("enchantGlint", true);
        }

        boolean passedAbility = false;
        boolean finishedAbilityLore = false;

        for (String string : lore) {
            if (string.toLowerCase().contains("this item can be reforged")) nbt.setBoolean("reforgeable", true);
        }

        for (int i = 0; i < lore.size(); i++) {
            String string = lore.get(i);

            List<String> list = Arrays.asList(ChatColor.stripColor(string.toLowerCase()).replaceAll("%", "").replaceAll("hp", "").replaceAll(" ", "").split(":+"));
            String line = ChatColor.stripColor(string.toLowerCase());

            try {
                int value = Integer.parseInt(list.get(1));

                if (line.startsWith("strength")) {
                    nbt.setInteger("strength", value);
                } else if (line.startsWith("health")) {
                    nbt.setInteger("health", value);
                } else if (line.startsWith("damage")) {
                    nbt.setInteger("damage", value);
                } else if (line.startsWith("crit chance")) {
                    nbt.setInteger("critChance", value);
                } else if (line.startsWith("crit damage")) {
                    nbt.setInteger("critDamage", value);
                } else if (line.startsWith("defense")) {
                    nbt.setInteger("defense", value);
                } else if (line.startsWith("true defense")) {
                    nbt.setInteger("trueDefense", value);
                } else if (line.startsWith("speed")) {
                    nbt.setInteger("speed", value);
                } else if (line.startsWith("intelligence")) {
                    nbt.setInteger("intelligence", value);
                } else if (line.startsWith("magic find")) {
                    nbt.setInteger("magicFind", value);
                } else if (line.startsWith("sea creature chance")) {
                    nbt.setInteger("seaCreatureChance", value);
                } else if (line.startsWith("pet luck")) {
                    nbt.setInteger("petLuck", value);
                } else if (line.startsWith("ferocity")) {
                    nbt.setInteger("ferocity", value);
                } else if (line.startsWith("ability damage")) {
                    nbt.setInteger("abilityDamage", value);
                } else if (line.startsWith("mana cost: ")) {
                    nbt.setInteger("abilityCost", value);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                if (line.startsWith("mana cost: ")) {
                    nbt.setInteger("abilityCost", Integer.parseInt(line.substring(11)));
                    continue;
                } else if (line.startsWith("item ability: ")) {
                    nbt.setBoolean("hasAbility", true);

                    String sub = line.substring(14);
                    String abilityName = WordUtils.capitalize(sub);
                    String type = "";

                    if (sub.contains("right click")) {
                        type = "Right Click";
                    } else if (sub.contains("left click")) {
                        type = "Left Click";
                    }

                    nbt.setString("abilityName", abilityName.replaceAll(type, ""));
                    nbt.setString("abilityType", type.toUpperCase());
                    passedAbility = true;
                    continue;
                } else if (line.startsWith("cooldown: ")) {
                    nbt.setString("abilityCooldown", line.substring(10));
                    continue;
                } else if (passedAbility && !finishedAbilityLore) {
                    List<String> ability = new ArrayList<>();
                    for (int j = i; j < lore.size(); j++) {
                        String raw = ChatColor.stripColor(lore.get(j)).toLowerCase();
                        if (raw.startsWith("this item can be reforged!") ||
                                raw.startsWith("cooldown: ") ||
                                raw.startsWith("mana cost: ") ||
                            j == lore.size() - 1){
                            i = j-1;
                            break;
                        }

                        ability.add(lore.get(j));
                    }

                    nbt.setString("abilityDescription", ability.toString());
                    finishedAbilityLore = true;
                    continue;
                } else if (!line.isEmpty()) {
                    List<String> desc = new ArrayList<>();
                    for (int j = i; j < lore.size(); j++) {
                        String raw = ChatColor.stripColor(lore.get(j)).toLowerCase();
                        if (raw.startsWith("this item can be reforged!") ||
                            raw.startsWith("item ability: ") ||
                            j == lore.size() - 1){
                            i = j;
                            break;
                        }

                        desc.add(lore.get(j));
                    }

                    if (desc.size() > 1) {
                        nbt.setString("description", desc.toString());
                    }
                }

                if (i == lore.size() - 1) {
                    nbt.setString("rarity", lore.get(i));
                }
            }
        }

        if (!nbt.hasKey("description")) {
            nbt.setString("description", "placeholder description");
        }

        if (!passedAbility) {
            nbt.setString("abilityDescription", "placeholder description");
        }

        if (!nbt.hasKey("enchantments")) {
            nbt.setString("enchantments", "[]");
        }

        ItemBase base = new ItemBase(nbt.getItem());

        return base.getStack();
    }
}
