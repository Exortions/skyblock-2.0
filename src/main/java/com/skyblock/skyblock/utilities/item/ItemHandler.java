package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.Skyblock;
import de.tr7zw.nbtapi.NBTItem;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemHandler {
    private final HashMap<String, ItemStack> items = new HashMap<>();
    private final Skyblock skyblock;

    public ItemHandler(Skyblock plugin) {
        this.skyblock = plugin;
    }

    public void init() {
        skyblock.sendMessage("Initializing Items...");

        File folder = new File(skyblock.getDataFolder() + File.separator + "items");

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)));

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

                meta.setLore(lore);

                item.setItemMeta(meta);

                net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
                nms.setTag(MojangsonParser.parse(nbt));

                register(file.getName(), CraftItemStack.asBukkitCopy(nms));
            } catch (MojangsonParseException | ParseException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        skyblock.sendMessage("Finished Initializing Items.");
    }

    public void register(String id, ItemStack item) {
        items.put(id, parseLore(item));
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
                if (line.startsWith("this item can be reforged!")) {
                    nbt.setBoolean("reforgeable", true);
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
                    nbt.setInteger("abilityCost", 0);
                } else if (line.startsWith("cooldown: ")) {
                    nbt.setString("abilityCooldown", line.substring(10));
                }

                if (i == lore.size() - 1) {
                    nbt.setString("rarity", lore.get(i));
                }
            }
        }

        nbt.setString("abilityDescription", "placeholder description");
        nbt.setString("description", "placeholder description");

        ItemBase base = new ItemBase(nbt.getItem());

        return base.getStack();
    }
}
