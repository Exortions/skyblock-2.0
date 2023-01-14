package com.skyblock.skyblock.features.slayer;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.utilities.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class SlayerBoss extends SkyblockEntity {

    private static final List<Integer> XP_REWARDS = Arrays.asList(5, 25, 100, 500);

    private final SlayerType slayerType;
    private final double displayHeight;
    private final Player spawner;
    private final int rewardXp;
    private ArmorStand display;
    private final int level;
    private long startTime;

    @Setter
    private boolean failed;

    public SlayerBoss(EntityType type, SlayerType slayerType, Player spawner, int level, double displayHeight) {
        super(type);

        this.level = level;
        this.slayerType = slayerType;
        this.displayHeight = displayHeight;
        this.spawner = spawner;

        this.lifeSpan = 180 * 20;
        this.failed = false;

        this.rewardXp = XP_REWARDS.get(level - 1);
    }

    @Override
    public Entity spawn(Location location) {
        display = location.getWorld().spawn(location, ArmorStand.class);

        display.setVisible(false);
        display.setVisible(false);
        display.setGravity(false);
        display.setCustomNameVisible(true);

        startTime = System.currentTimeMillis();

        SlayerQuest quest = Skyblock.getPlugin().getSlayerHandler().getSlayer(spawner).getQuest();
        if (quest != null) quest.setTimeToSpawn(System.currentTimeMillis() - quest.getTimeToSpawn());
        
        return super.spawn(location);
    }

    @Override
    protected void tick() {
        if (!getVanilla().hasMetadata("isSlayerBoss")) {
            getVanilla().setMetadata("isSlayerBoss", new FixedMetadataValue(Skyblock.getPlugin(Skyblock.class), true));
        }

        display.setCustomName(ChatColor.RED + getDespawnDelayDisplay());

        Entity entity = getVanilla();

        entity.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + getEntityData().level + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "☠ " + ChatColor.WHITE + getEntityData().entityName + " " + ChatColor.GREEN + Util.format(Math.max(0, getEntityData().health)) + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + Util.format(getEntityData().maximumHealth) + ChatColor.RED + "❤");
        display.teleport(new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY() + displayHeight, entity.getLocation().getZ()));

        if (getVanilla().isDead()) display.remove();
    }

    public String getDespawnDelayDisplay(){
        int ticks = getLifeSpan();
        long minute = ticks / 1200;
        long second = ticks / 20 - minute*60;
        return "" + Math.round(minute) + ":" + (String.valueOf(second).length() == 1 ? "0" + Math.round(second) : Math.round(second));
    }

    @Override
    protected void onDeath() {
        if (failed) {
            display.remove();
            return;
        }

        SlayerQuest quest = getPlugin().getSlayerHandler().getSlayer(spawner).getQuest();

        spawner.playSound(spawner.getLocation(), Sound.LEVEL_UP, 1, 2);
        spawner.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "NICE! SLAYER BOSS SLAIN!");
        spawner.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "→ " + ChatColor.GRAY + "Talk to Maddox to claim your " + getSlayerType().getAlternative() + " Slayer XP!");

        if (quest != null) {
            quest.setTimeToKill(System.currentTimeMillis() - startTime);
            quest.setState(SlayerQuest.QuestState.FINISHED);
        }
    }

    @Override
    public void onDespawn() {
        super.onDespawn();

        SlayerQuest quest = getPlugin().getSlayerHandler().getSlayer(spawner).getQuest();
        if (quest != null) quest.fail();
    }

    @Override
    public void setLifeSpan(int lifeSpan) { }

    public String getName() {
        return getEntityData().entityName + " " + getLevel();
    }
}
