package com.skyblock.skyblock.utilities.item;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.features.crafting.SkyblockRecipe;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantment;
import com.skyblock.skyblock.features.enchantment.SkyblockEnchantmentHandler;
import com.skyblock.skyblock.features.items.browser.BrowserCategory;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.features.pets.PetType;
import com.skyblock.skyblock.features.potions.PotionEffect;
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
import java.util.function.Function;

@Getter
public class ItemHandler {
    private final HashMap<String, ItemStack> items = new HashMap<>();
    private final HashMap<ItemStack, String> reversed = new HashMap<>();
    private final HashMap<BrowserCategory, List<ItemStack>> categories = new HashMap<>();
    private final Skyblock skyblock;

    public static final List<String> POTIONS = new ArrayList<String>() {{
        add("strength");
        add("speed");
        add("healing");
    }};

    public static final List<String> ITEM_EXCLUSIONS = new ArrayList<String>() {{
        add("fancy_sword.json");
        add("raider_axe.json");
        add("midas_sword.json");
    }};

    public static final HashMap<String, Function<ItemBase, ItemBase>> SKYBLOCK_ID_TO_ITEM = new HashMap<String, Function<ItemBase, ItemBase>>() {{
        put("raider_axe", (base -> {
            NBTItem nbtItem = new NBTItem(base.createStack().clone());

            List<String> description = Arrays.asList(Util.buildLore(
                    "&7Earn &6+20 coins &7from monster kills (level &e10+ &7only)\n\n&c+1 Damage &7per &c500 &7kills\n&8Max +35\n&7Kills: &c" + Util.formatInt(nbtItem.getInteger("raider_axe_kills")) +
                            "\n\n&c+1❁ Strength &7per &e500 &7wood\n&8Sums wood collections, max +100\n&7Wood collections: &e" + Util.formatInt(nbtItem.getInteger("raider_axe_collection")) + "\n" + " ")
            );

            NBTItem nbt = new NBTItem(base.getStack());

            int currentKills = nbt.getInteger("raider_axe_kills");
            int appliedKills = 0;

            int damage = 0;

            for (int i = 0; i < currentKills / 500; i++) {
                if (appliedKills >= 35) break;

                damage++;
                appliedKills++;
            }

            base.setDamage(base.getDamage() + damage);

            base.setDescription(description);
            base.createStack();

            nbt = new NBTItem(base.getStack());

            nbt.setInteger("raider_axe_kills", nbtItem.getInteger("raider_axe_kills"));
            nbt.setInteger("raider_axe_collection", nbtItem.getInteger("raider_axe_collection"));

            base.setStack(nbt.getItem());

            return base;
        }));
    }};

    public ItemHandler(Skyblock plugin) {
        this.skyblock = plugin;
    }

    public void init() {
        File folder = new File(skyblock.getDataFolder() + File.separator + "items");

        try {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                try {
                    JSONParser parser = new JSONParser();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
                    Object obj = parser.parse(bufferedReader);

                    JSONObject jsonObject = (JSONObject) obj;
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
                    meta.spigot().setUnbreakable(true);

                    item.setItemMeta(meta);

                    net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
                    nms.setTag(MojangsonParser.parse(nbt));

                    NBTItem nbtItem = new NBTItem(CraftItemStack.asBukkitCopy(nms));
                    nbtItem.setString("skyblockId", file.getName().replace(".json", "").toLowerCase());

                    ItemStack clone = nbtItem.getItem().clone();
                    ItemMeta cloneMeta = clone.getItemMeta();
                    cloneMeta.spigot().setUnbreakable(true);

                    List<String> clonedLore = cloneMeta.getLore();

                    if (clonedLore.contains(ChatColor.YELLOW + "Click to view recipe!")) {
                        clonedLore.remove(clonedLore.size() - 1);
                        clonedLore.remove(clonedLore.size() - 1);
                    }

                    cloneMeta.setLore(clonedLore);
                    clone.setItemMeta(cloneMeta);

                    register(file.getName(), clone);

                    if (jsonObject.get("recipe") != null) {
                        JSONObject recipe = (JSONObject) jsonObject.get("recipe");
                        Skyblock.getPlugin(Skyblock.class).getRecipeHandler().getRecipes().add(
                                new SkyblockRecipe(recipe, getItem(file.getName())));
                    }

                    bufferedReader.close();
                } catch (MojangsonParseException | ParseException | IOException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        } catch (NullPointerException ex) {
            Skyblock.getPlugin().sendMessage("&c=============================================");
            Skyblock.getPlugin().sendMessage("&c  &4ERROR&c &cCould not find items folder!");
            Skyblock.getPlugin().sendMessage("&c  &4ERROR&c &cPlease make sure you have");
            Skyblock.getPlugin().sendMessage("&c  &4ERROR&c &cthe items folder in the plugin");
            Skyblock.getPlugin().sendMessage("&c  &4ERROR&c &cdata folder, located at:");
            Skyblock.getPlugin().sendMessage("&c  &4ERROR&c &chttps://github.com/Exortions/skyblock-2.0/tree/master/dependencies/skyblock/");
            Skyblock.getPlugin().sendMessage("&c=============================================");

            Bukkit.getPluginManager().disablePlugin(Skyblock.getPlugin());
            return;
        }

        registerCustomItems();
    }

    public void registerCustomItems() {
        skyblock.sendMessage("Registering custom items...");

        long start = System.currentTimeMillis();

        items.put("fancy_sword", new ItemBase(
                Material.GOLD_SWORD,
                ChatColor.WHITE + "Fancy Sword",
                Reforge.NONE,
                1,
                new ArrayList<>(),
                new ArrayList<ItemEnchantment>() {{
                    add(new ItemEnchantment(skyblock.getEnchantmentHandler().getEnchantment("first_strike"), 1));
//                    add(new ItemEnchantment(skyblock.getEnchantmentHandler().getEnchantment("scavenger"), 1))
                    add(new ItemEnchantment(skyblock.getEnchantmentHandler().getEnchantment("sharpness"), 2));
//                    add(new ItemEnchantment(skyblock.getEnchantmentHandler().getEnchantment("vampirism"), 1));
                }},
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
            if (r.equals(Rarity.VERY_SPECIAL)) continue;
            if (r.equals(Rarity.SPECIAL)) continue;
            if (r.equals(Rarity.MYTHIC)) continue;
            if (r.equals(Rarity.DIVINE)) continue;

            for (PetType type : PetType.values()) {
                Pet pet = type.newInstance();
                if (pet == null) continue;
                pet.setRarity(r);
                items.put(type.name() + "_" + r.toString().toUpperCase() + ".json", pet.toItemStack());
            }
        }

        for (String pot : POTIONS) {
            for (int i = 0; i < PotionEffect.getMaxLevelsAndColors.get(pot).getFirst(); i++) {
                items.put((pot + "_" + (i + 1)).toUpperCase() + ".json", Util.createPotion(pot, i + 1, 12000));
            }
        }

        SkyblockEnchantmentHandler enchantmentHandler = skyblock.getEnchantmentHandler();

        for (SkyblockEnchantment enchant : enchantmentHandler.getEnchantments()) {
            for (int i = 1; i <= enchant.getMaxLevel(); i++) {
                List<String> desc = new ArrayList<>(Arrays.asList(ChatColor.GRAY + "Use this on an item in an Anvil", ChatColor.GRAY + "to apply it!", " "));
                ItemBase base = new ItemBase(Material.ENCHANTED_BOOK, ChatColor.WHITE + "Enchanted Book", Reforge.NONE, 1, desc, Collections.singletonList(new ItemEnchantment(enchant, i)), true, false, "", Arrays.asList("placeholder description", "placeholder description", "placeholder description"), "", 0, "", "common", "enchanted_book_" + enchant.getName() + "_" + i, 0, 0, 0, 0, 0, 0, 0, 0, 0, false);
                items.put(enchant.getName().toUpperCase() + ";" + i + ".json",
                        base.createStack());
            }
        }

        skyblock.sendMessage("Sucessfully Registered custom items [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.WHITE + "]");
    }

    public void register(String id, ItemStack item) {
        for (String potion : POTIONS) {
            if (item.getItemMeta().getDisplayName().toLowerCase().contains(potion) && item.getType().equals(Material.POTION))
                return;
        }

        items.put(id, parseLore(item));
        reversed.put(parseLore(item), id.replace(".json", ""));
    }

    private static final List<String> stackableHeads = Arrays.asList("fragment");
    public ItemStack getItem(String s) {
        if (!s.endsWith(".json")) {
            s = s.toUpperCase();
            s += ".json";
        }

        ItemStack item = items.get(s);

        if (item == null) return null;

        if (item.getType().equals(Material.SKULL_ITEM)) {

            for (String stackables : stackableHeads) {
                if (item.getItemMeta().getDisplayName().toLowerCase().contains(stackables)) return item.clone();
            }

            NBTItem nbt = new NBTItem(item.clone());
            nbt.setString(UUID.randomUUID().toString(), "dontstackanymoreplease");

            return nbt.getItem();
        }

        return item.clone();
    }

    public ItemStack getItem(ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        ItemStack skyblock = item;

        if (nbt.hasKey("skyblockId") && skyblock.getType() != Material.ENCHANTED_BOOK) {
            skyblock = getItem(Util.getSkyblockId(item));
        }

        if (item.getType().equals(Material.SKULL_ITEM)) {

            for (String stackables : stackableHeads) {
                if (item.getItemMeta().getDisplayName().toLowerCase().contains(stackables)) return item.clone();
            }

            nbt = new NBTItem(item.clone());
            nbt.setString(UUID.randomUUID().toString(), "dontstackanymoreplease");

            return nbt.getItem();
        }

        return skyblock.clone();
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
                                j == lore.size() - 1) {
                            i = j - 1;
                            break;
                        }

                        ability.add(lore.get(j));
                    }

                    StringBuilder abilityDescription = new StringBuilder();

                    for (String s : ability) {
                        abilityDescription.append("; ").append(s);
                    }

                    nbt.setString("abilityDescription", abilityDescription.substring(1, abilityDescription.length() - 1));
                    finishedAbilityLore = true;
                    continue;
                } else if (!line.isEmpty()) {
                    List<String> desc = new ArrayList<>();
                    for (int j = i; j < lore.size(); j++) {
                        String raw = ChatColor.stripColor(lore.get(j)).toLowerCase();
                        if (raw.startsWith("this item can be reforged!") ||
                                raw.startsWith("item ability: ") ||
                                j == lore.size() - 1) {
                            i = j;
                            break;
                        }

                        desc.add(lore.get(j));
                    }

                    if (desc.size() > 1) {
                        StringBuilder description = new StringBuilder();

                        for (String s : desc) {
                            description.append("; ").append(s);
                        }

                        nbt.setString("description", description.substring(1, description.length() - 1));
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
