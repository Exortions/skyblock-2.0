package com.skyblock.skyblock.features.skills;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerSkillXPChangeEvent;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.utilities.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class Skill {

    public static final List<Integer> XP = Arrays.asList(0, 50, 175, 375, 675, 1175, 1925, 2925, 4425, 6425, 9925, 14925, 22425, 32425, 47425,
            67425, 97425, 147425, 222425, 322425, 522425, 822425, 1222425, 1722425, 2322425, 3022425, 3822425, 4722425,
            5722425, 6822425, 8022425, 9322425, 10722425, 12222425, 13822425, 15522425, 17322425, 19222425, 21222425,
            23322425, 25522425, 27822425, 30222425, 32722425, 35322425, 38072425, 40972425, 44072425, 47472425, 51172425,
            55172425, 59472425, 64072425, 68972425, 74172425, 79672425, 85472425, 91572425, 97972425, 104672425, 111672425);

    public static final List<Integer> COINS = Arrays.asList(0, 25, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300,
            1400, 1500, 1600, 1700, 1800, 1900, 2000, 2200, 2400, 2600, 2800, 3000, 3500, 4000, 5000, 6000, 7500, 10000, 12500,
            15000, 17500, 20000, 25000, 30000, 35000, 40000, 45000, 50000, 60000, 70000, 80000, 90000, 100000, 125000, 150000,
            175000, 200000, 250000, 300000, 350000, 400000, 450000, 500000, 600000, 700000, 800000, 1000000);

    public static final List<String> SKILLS = Arrays.asList("Farming", "Mining", "Combat", "Foraging", "Fishing", "Enchanting", "Alchemy");

    private final String name;
    private final String alternate;
    private final String description;
    private final ItemStack guiIcon;
    private final ItemStack guiMilestoneIcon;

    public Skill(String name, String alternate, String description, ItemStack guiIcon, ItemStack guiMilestoneIcon) {
        this.name = name;
        this.alternate = alternate;
        this.description = description;
        this.guiIcon = guiIcon;
        this.guiMilestoneIcon = guiMilestoneIcon;
    }

    public static int getLevel(double xp) {
        if (xp == 0.0)
            return 0;
        if (xp >= 55172425.0)
            return 50;
        for (int i = XP.size() - 1; i >= 0; i--) {
            if (XP.get(i) < xp)
                return i;
        }
        return 60;
    }
    public static double getNextXPGoal(double xp) {
        if (xp >= 55172425.0)
            return 0.0;
        for (int i = 0; i < XP.size(); i++) {
            int goal = XP.get(i);
            if (goal > xp)
                return goal - (i - 1 < 0 ? 0 : XP.get(i - 1));
        }
        return 0.0;
    }

    public static double getXP(Skill skill, SkyblockPlayer player) {
        return (double) player.getValue("skill." + skill.name.toLowerCase() + ".exp");
    }

    public static void reward(Skill skill, double xp, SkyblockPlayer player) {
        double curr = (double) player.getValue("skill." + skill.getName().toLowerCase() + ".exp");
        player.setValue("skill." + skill.getName().toLowerCase() + ".exp", curr + xp);
        skill.update(player, (int) (curr));
        player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.ORB_PICKUP, 1f, 2f);

        Bukkit.getPluginManager().callEvent(new SkyblockPlayerSkillXPChangeEvent(skill, player, xp));

        Pet pet = player.getPet();

        if (pet != null) {
            pet.unequip(player);
            pet.setInGui(true);
            player.removePet(pet.toItemStack());
            player.setValue("pets.equip", null);
            pet.setInGui(false);

            double petXp = (pet.getSkill().equals(skill)) ? xp : xp / 4;

            if (!pet.getSkill().equals(skill)) {
                if (skill.equals(new Enchanting()) || skill.equals(new Alchemy())) {
                    petXp = xp / 12;
                }

                if (skill.equals(new Mining()) || skill.equals(new Foraging())) {
                    petXp = petXp * 1.5;
                }
            }

            int prevLevel = Pet.getLevel(pet.getXp(), pet.getRarity());
            pet.setXp(pet.getXp() + petXp);
            int level = Pet.getLevel(pet.getXp(), pet.getRarity());
            if (level > prevLevel) {
                player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.LEVEL_UP, 1, 2);
                player.getBukkitPlayer().sendMessage(ChatColor.GREEN + "Your " + pet.getColoredName() + ChatColor.GREEN + " levelled up to level " + ChatColor.BLUE + level + ChatColor.GREEN + "!");
            }

            pet.equip(player);
            player.addPet(pet.toItemStack());
            player.setValue("pets.equip", pet.toItemStack());
        }

        int level = getLevel(getXP(skill, player));

        String actionBar = ChatColor.RED + "" + player.getStat(SkyblockStat.HEALTH) + "/" + player.getStat(SkyblockStat.MAX_HEALTH) + "❤   " +
                ChatColor.DARK_AQUA + "+" + xp + " " + skill.name + " (" + (getXP(skill, player) - XP.get(level)) + "/" +  getNextXPGoal(getXP(skill, player)) + ") "
                +  ChatColor.AQUA + "" + player.getStat(SkyblockStat.MANA) + "/" + player.getStat(SkyblockStat.MAX_MANA) + "✎ Mana";
        player.setActionBar(actionBar);

        player.delay(player::resetActionBar, 3);

        if (skill.getName().equals("Combat") && player.hasActiveSlayer() && player.getExtraData("lastKilledType").equals(Skyblock.getPlugin(Skyblock.class).getSlayerHandler().getSlayer(player.getBukkitPlayer()).getQuest().getMobType()))
            Skyblock.getPlugin().getSlayerHandler().addExp(player.getBukkitPlayer(), xp);
    }

    public static Skill parseSkill(String s) {
        switch (s) {
            case "Combat":
                return new Combat();
            case "Alchemy":
                return new Alchemy();
            case "Enchanting":
                return new Enchanting();
            case "Farming":
                return new Farming();
            case "Foraging":
                return new Foraging();
            case "Mining":
                return new Mining();
            case "Fishing":
                return new Fishing();
            default:
                return null;
        }
    }

    public abstract List<String> getRewards(int level, int lastLevel);
    public abstract void levelUp(SkyblockPlayer player, int level);

    public void update(SkyblockPlayer player, int prev) {
        int prevLevel = getLevel(prev);
        int newLevel = getLevel(getXP(player));

        if (newLevel > prevLevel) {
            for (int level = prevLevel + 1; level <= newLevel; level++) {
                player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.LEVEL_UP, 1f, 1f);

                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.DARK_AQUA).append(ChatColor.BOLD).append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n");
                builder.append(ChatColor.AQUA).append(ChatColor.BOLD).append("  SKILL LEVEL UP ").append(ChatColor.RESET).append(ChatColor.DARK_AQUA).append(getName()).append(" ");

                if (prevLevel != 0) builder.append(ChatColor.DARK_GRAY).append(Util.toRoman(prevLevel)).append("➜");
                builder.append(ChatColor.DARK_AQUA).append(Util.toRoman(level)).append("\n");
                builder.append(" \n");
                builder.append(ChatColor.GREEN).append(ChatColor.BOLD).append("  REWARDS\n");
                builder.append(ChatColor.YELLOW).append("   ").append(getAlternate()).append(" ").append(Util.toRoman(level)).append("\n");

                for (String s : getRewards(level, prevLevel)) {
                    builder.append("   ").append(s).append("\n");
                }
                builder.append(ChatColor.DARK_GRAY).append("   +").append(ChatColor.GOLD).append(Util.formatInt(COINS.get(level))).append(ChatColor.GRAY).append(" Coins");

                builder.append("\n").append(ChatColor.DARK_AQUA).append(ChatColor.BOLD).append("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                player.addCoins(COINS.get(level));
                player.getBukkitPlayer().sendMessage(builder.toString());

                levelUp(player, level);
            }
        }
    }

    public double getXP(SkyblockPlayer player) {
        return (double) player.getValue("skill." + name.toLowerCase() + ".exp");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Skill)) return false;

        Skill skill = (Skill) obj;

        return skill.getName().equals(this.getName());
    }
}
