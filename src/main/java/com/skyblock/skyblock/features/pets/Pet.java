package com.skyblock.skyblock.features.pets;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.Rarity;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public abstract class Pet {

    private String name;
    private Rarity rarity;
    private double xp;
    private int level;
    private boolean inGui;
    private boolean active;
    private UUID uuid;

    protected static final List<Integer> COMMON_XP = Arrays.asList(0, 100, 210, 330, 460, 605, 765, 940, 1130, 1340, 1570, 1820, 2095, 2395, 2725, 3085, 3485, 3925, 4415,
            4955, 5555, 6215, 6945, 7745, 8625, 9585, 10635, 11785, 13045, 14425, 15935, 17585, 19385, 21345, 23475, 25785, 28285, 30985, 33905, 37065, 40485, 44185, 48185,
            52535, 57285, 62485, 68185, 74485, 81485, 89285, 97985, 107685, 118485, 130485, 143785, 158485, 174685, 192485, 211985, 233285, 256485, 281685, 309085, 338885,
            371285, 406485, 444685, 486085, 530885, 579285, 631485, 687685, 748085, 812885, 882285, 956485, 1035685, 1120385, 1211085, 1308285, 1412485, 1524185, 1643885,
            1772085, 1909285, 2055985, 2212685, 2380385, 2560085, 2752785, 2959485, 3181185, 3418885, 3673585, 3946285, 4237985, 4549685, 4883385, 5241085, 5624785);

    protected static final List<Integer> UNCOMMON_XP = Arrays.asList(0, 175, 365, 575, 805, 1055, 1330, 1630, 1960, 2320, 2720, 3160, 3650, 4190, 4790, 5450,
            6180, 6980, 7860, 8820, 9870, 11020, 12280, 13660, 15170, 16820, 18620, 20580, 22710, 25020, 27520, 30220, 33140, 36300, 39720, 43420, 47420, 51770, 56520,
            61720, 67420, 73720, 80720, 88520, 97220, 106920, 117720, 129720, 143020, 157720, 173920, 191720, 211220, 232520, 255720, 280920, 308320, 338120, 370520,
            405720, 443920, 485320, 530120, 578520, 630720, 686920, 747320, 812120, 881520, 955720, 1034920, 1119620, 1210320, 1307520, 1411720, 1523420, 1643120, 1771320,
            1908520, 2055220, 2211920, 2379620, 2559320, 2752020, 2958720, 3180420, 3418120, 3672820, 3945520, 4237220, 4548920, 4882620, 5240320, 5624020, 6035720,
            6477420, 6954120, 7470820, 8032520, 8644220);

    protected static final List<Integer> RARE_XP = Arrays.asList(0, 275, 575, 905, 1265, 1665, 2105, 2595, 3135, 3735, 4395, 5125, 5925, 6805, 7765, 8815, 9965,
            11225, 12605, 14115, 15765, 17565, 19525, 21655, 23965, 26465, 29165, 32085, 35245, 38665, 42365, 46365, 50715, 55465, 60665, 66365, 72665, 79665, 87465,
            96165, 105865, 116665, 128665, 141965, 156665, 172865, 190665, 210165, 231465, 254665, 279865, 307265, 337065, 369465, 404665, 442865, 484265, 529065, 577465,
            629665, 685865, 746265, 811065, 880465, 954665, 1033865, 1118565, 1209265, 1306465, 1410665, 1522365, 1642065, 1770265, 1907465, 2054165, 2210865, 2378565,
            2558265, 2750965, 2957665, 3179365, 3417065, 3671765, 3944465, 4236165, 4547865, 4881565, 5239265, 5622965, 6034665, 6476365, 6953065, 7469765, 8031465, 8643165,
            9309865, 10036565, 10828265, 11689965, 12626665);

    protected static final List<Integer> EPIC_XP = Arrays.asList(0, 440, 930, 1470, 2070, 2730, 3460, 4260, 5140, 6100, 7150, 8300, 9560, 10940, 12450, 14100, 15900,
            17860, 19990, 22300, 24800, 27500, 30420, 33580, 37000, 40700, 44700, 49050, 53800, 59000, 64700, 71000, 78000, 85800, 94500, 104200, 115000, 127000, 140300,
            155000, 171200, 189000, 208500, 229800, 253000, 278200, 305600, 335400, 367800, 403000, 441200, 482600, 527400, 575800, 628000, 684200, 744600, 809400, 878800,
            953000, 1032200, 1116900, 1207600, 1304800, 1409000, 1520700, 1640400, 1768600, 1905800, 2052500, 2209200, 2376900, 2556600, 2749300, 2956000, 3177700, 3415400,
            3670100, 3942800, 4234500, 4546200, 4879900, 5237600, 5621300, 6033000, 6474700, 6951400, 7468100, 8029800, 8641500, 9308200, 10034900, 10826600, 11688300, 12625000,
            13641700, 14743400, 15935100, 17221800, 18608500);

    protected static final List<Integer> LEGENDARY_XP = Arrays.asList(0, 660, 1390, 2190, 3070, 4030, 5080, 6230, 7490, 8870, 10380, 12030, 13830, 15790, 17920, 20230,
            22730, 25430, 28350, 31510, 34930, 38630, 42630, 46980, 51730, 56930, 62630, 68930, 75930, 83730, 92430, 102130, 112930, 124930, 138230, 152930, 169130, 186930,
            206430, 227730, 250930, 276130, 303530, 333330, 365730, 400930, 439130, 480530, 525330, 573730, 625930, 682130, 742530, 807330, 876730, 950930, 1030130, 1114830,
            1205530, 1302730, 1406930, 1518630, 1638330, 1766530, 1903730, 2050430, 2207130, 2374830, 2554530, 2747230, 2953930, 3175630, 3413330, 3668030, 3940730, 4232430,
            4544130, 4877830, 5235530, 5619230, 6030930, 6472630, 6949330, 7466030, 8027730, 8639430, 9306130, 10032830, 10824530, 11686230, 12622930, 13639630, 14741330,
            15933030, 17219730, 18606430, 20103130, 21719830, 23466530, 25353230);

    private static List<Integer> getXPGoal(Rarity rarity)
    {
        List<Integer> goals;
        switch (rarity)
        {
            case COMMON:
                goals = COMMON_XP;
                break;
            case UNCOMMON:
                goals = UNCOMMON_XP;
                break;
            case RARE:
                goals = RARE_XP;
                break;
            case EPIC:
                goals = EPIC_XP;
                break;
            default:
                goals = LEGENDARY_XP;
                break;
        }
        return goals;
    }

    public static int getLevel(double xp, Rarity rarity)
    {
        if (xp < 0.0)
            return -1;
        List<Integer> goals = getXPGoal(rarity);
        for (int i = 0; i < goals.size(); i++)
        {
            if (goals.get(i) > xp)
                return i;
        }
        return 100;
    }

    private static double getXP(int level, Rarity rarity) {
        if ((level - 1) < 0 || (level - 1) > 99)
            return -1.0;
        return getXPGoal(rarity).get(level);
    }

    private static void addIntLore(String name, double value, List<String> lore, int level) {
        long fin = Math.round(value * level);
        if (value != 0.0) lore.add(ChatColor.GRAY + name + ": " + ChatColor.GREEN + (fin >= 0 ? "+" : "") + fin);
    }

    private static void addPercentLore(String name, double value, List<String> lore, int level) {
        long fin = Math.round((value * 100.0) * level);
        if (value != 0.0) lore.add(ChatColor.GRAY + name + ": " + ChatColor.GREEN + (fin >= 0 ? "+" : "") + fin + "%");
    }

    public static Pet getPet(ItemStack item) {
        if (!Util.notNull(item)) return null;

        NBTItem nbtItem = new NBTItem(item);
        double xp = nbtItem.getDouble("xp");
        String id = nbtItem.getString("id");
        Rarity rarity = Rarity.valueOf(nbtItem.getString("rarity"));
        UUID uuid = UUID.fromString(nbtItem.getString("uuid"));

        try {
            Pet pet = PetType.valueOf(id.toUpperCase().replaceAll(" ", "_")).newInstance();

            if (pet == null) return null;

            pet.setRarity(rarity);
            pet.setUuid(uuid);
            pet.setXp(xp);

            return pet;
        } catch (Exception e) {
            return null;
        }
    }

    public Pet(String name) {
        this.name = name;
        this.xp = 0.0;
        this.level = 1;
        this.rarity = Rarity.COMMON;
        this.inGui = false;
        this.active = false;
        this.uuid = UUID.randomUUID();
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("rarity", rarity.name());
        nbtItem.setBoolean("active", active);
        nbtItem.setBoolean("inGui", inGui);
        nbtItem.setString("id", name);
        nbtItem.setDouble("xp", xp);
        nbtItem.setInteger("level", level);
        nbtItem.setBoolean("isPet", true);
        nbtItem.setString("uuid", uuid.toString());

        item = Util.idToSkull(nbtItem.getItem(), getSkull());

        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        int level = getLevel(xp, rarity);

        meta.setDisplayName(ChatColor.GRAY + "[Lvl " + level + "] " + rarity.getColor() + name);

        lore.add(ChatColor.DARK_GRAY + getSkill().getName() + " Pet");
        lore.add(" ");
        addIntLore("Magic Find", (getPerMagicFind() * 100.0) + getBaseMagicFind() / level, lore, level);
        addPercentLore("Crit Damage", getPerCritDamage() + getBaseCritDamage() / level, lore, level);
        addPercentLore("Crit Chance", getPerCritChance() + getBaseCritChance() / level, lore, level);

        double health = getPerHealth();
        if (health > 0.0) lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + Math.round(health * level) + " HP");

        addIntLore("Ferocity", getPerFerocity() + getBaseFerocity() / level, lore, level);
        addIntLore("Strength", getPerStrength() + getBaseStrength() / level, lore, level);
        addIntLore("Defense", getPerDefense() + getBaseDefense() / level, lore, level);
        addIntLore("Speed", getPerSpeed() + getBaseSpeed() / level, lore, level);
        addIntLore("Intelligence", getPerIntelligence() + getBaseIntelligence() / level, lore, level);

        for (PetAbility ability : getAbilities(level)) {
            if (ability == null) continue;

            if (rarity.getLevel() < ability.getRequiredRarity().getLevel()) continue;
            lore.add(" ");
            lore.add(ChatColor.GOLD + ability.getName());
            for (String line : ability.getDescription()) lore.add(ChatColor.GRAY + line);
        }

        lore.add(" ");

        if (level != 100) {
            int next = level + 1;
            double progress = xp;
            int goal = (int) (getXP(level, rarity));

            double percent = (progress / goal) * 100.0;

            lore.add(ChatColor.GRAY + "Progress to Level " + next + ": " + (percent < 100 ? ChatColor.YELLOW : ChatColor.GREEN) + Math.round(percent) + "%");
            lore.add(Util.getProgressBar(percent * 10, 20, goal / 20f) + " " + ChatColor.YELLOW + Util.formatInt((int) progress) + "/" + Util.format(goal));
        } else {
            lore.add(ChatColor.AQUA + "" + ChatColor.BOLD + "MAX LEVEL");
        }

        lore.add(" ");
        lore.add(rarity.getColor() + "" + ChatColor.BOLD + rarity.name());
        lore.add(" ");

        if (!inGui) lore.add(ChatColor.YELLOW + "Right-Click to add to menu!");

        if (inGui) {
            lore.add((active ? ChatColor.RED + "Click to despawn!" : ChatColor.YELLOW + "Click to summon!"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public abstract Skill getSkill();

    public abstract List<PetAbility> getAbilities(int level);
    public abstract String getSkull();

    public double getPerHealth() {
        return 0.0;
    }
    public double getPerDefense() {
        return 0.0;
    }
    public double getPerStrength() {
        return 0.0;
    }
    public double getPerIntelligence() {
        return 0.0;
    }
    public double getPerSpeed() {
        return 0.0;
    }
    public double getPerCritChance() {
        return 0.0;
    }
    public double getPerCritDamage() {
        return 0.0;
    }
    public double getPerMagicFind() {
        return 0.0;
    }
    public double getPerFerocity() {
        return 0.0;
    }

    public double getBaseHealth() {
        return 0.0;
    }
    public double getBaseDefense() {
        return 0.0;
    }
    public double getBaseStrength() {
        return 0.0;
    }
    public double getBaseIntelligence() {
        return 0.0;
    }
    public double getBaseSpeed() {
        return 0.0;
    }
    public double getBaseCritChance() {
        return 0.0;
    }
    public double getBaseCritDamage() {
        return 0.0;
    }
    public double getBaseMagicFind() {
        return 0.0;
    }
    public double getBaseFerocity() {
        return 0.0;
    }

    public String getColoredName() {
        return getRarity().getColor() + getName();
    }

    public void equip(SkyblockPlayer player) {
        level = getLevel(xp, rarity);

        player.addStat(SkyblockStat.HEALTH, (int) (level * (getPerHealth() + getBaseHealth() / level)));
        player.addStat(SkyblockStat.MAX_HEALTH, (int) (level * (getPerHealth() + getBaseHealth() / level)));
        player.addStat(SkyblockStat.DEFENSE, (int) (level * (getPerDefense() + getBaseDefense() / level)));
        player.addStat(SkyblockStat.STRENGTH, (int) (level * (getPerStrength() + getBaseStrength() / level)));
        player.addStat(SkyblockStat.MANA, (int) (level * (getPerIntelligence() + getBaseIntelligence() / level)));
        player.addStat(SkyblockStat.MAX_MANA, (int) (level * (getPerIntelligence() + getBaseIntelligence() / level)));
        player.addStat(SkyblockStat.SPEED, (int) (level * (getPerSpeed() + getBaseSpeed() / level)));
        player.addStat(SkyblockStat.CRIT_DAMAGE, (int) (level * (getPerCritDamage() + getBaseCritDamage() / level)));
        player.addStat(SkyblockStat.CRIT_CHANCE, (int) (level * (getPerCritChance() + getBaseCritChance() / level)));
        player.addStat(SkyblockStat.MAGIC_FIND, (int) (level * (getPerMagicFind() + getBaseMagicFind() / level)));
        player.addStat(SkyblockStat.FEROCITY, (int) (level * (getPerFerocity() + getBaseFerocity() / level)));

        for (PetAbility ability : getAbilities(getLevel())) {
            if (rarity.getLevel() >= ability.getRequiredRarity().getLevel())
                try {
                    ability.onEquip(player);
                } catch (UnsupportedOperationException ignored) {}
        }
    }

    public void unequip(SkyblockPlayer player) {
        level = getLevel(xp, rarity);

        player.subtractStat(SkyblockStat.HEALTH, (int) (level * (getPerHealth() + getBaseHealth() / level)));
        player.subtractStat(SkyblockStat.MAX_HEALTH, (int) (level * (getPerHealth() + getBaseHealth() / level)));
        player.subtractStat(SkyblockStat.DEFENSE, (int) (level * (getPerDefense() + getBaseDefense() / level)));
        player.subtractStat(SkyblockStat.STRENGTH, (int) (level * (getPerStrength() + getBaseStrength() / level)));
        player.subtractStat(SkyblockStat.MANA, (int) (level * (getPerIntelligence() + getBaseIntelligence() / level)));
        player.subtractStat(SkyblockStat.MAX_MANA, (int) (level * (getPerIntelligence() + getBaseIntelligence() / level)));
        player.subtractStat(SkyblockStat.SPEED, (int) (level * (getPerSpeed() + getBaseSpeed() / level)));
        player.subtractStat(SkyblockStat.CRIT_DAMAGE, (int) (level * (getPerCritDamage() + getBaseCritDamage() / level)));
        player.subtractStat(SkyblockStat.CRIT_CHANCE, (int) (level * (getPerCritChance() + getBaseCritChance() / level)));
        player.subtractStat(SkyblockStat.MAGIC_FIND, (int) (level * (getPerMagicFind() + getBaseMagicFind() / level)));
        player.subtractStat(SkyblockStat.FEROCITY, (int) (level * (getPerFerocity() + getBaseFerocity() / level)));

        for (PetAbility ability : getAbilities(getLevel())) {
            if (rarity.getLevel() >= ability.getRequiredRarity().getLevel())
                try {
                    ability.onUnequip(player);
                } catch (UnsupportedOperationException ignored) { }
        }
    }

    public void onStatChange(SkyblockPlayer skyblockPlayer, SkyblockStat stat, double amount) {
        for (PetAbility ability : getAbilities(getLevel())) {
            try {
                ability.onStatChange(skyblockPlayer, stat, amount);
            } catch (UnsupportedOperationException ignored) { }
        }
    }

    public void onDamage(SkyblockPlayerDamageEntityEvent e) {
        for (PetAbility ability : getAbilities(getLevel())) {
            try {
                ability.onDamage(e);
            } catch (UnsupportedOperationException ignored) { }
        }
    }
}
