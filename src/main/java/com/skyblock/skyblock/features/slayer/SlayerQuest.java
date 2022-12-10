package com.skyblock.skyblock.features.slayer;

import com.skyblock.skyblock.utilities.Util;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Data
public class SlayerQuest {

    public enum QuestState {
        SUMMONING,
        FIGHTING,
        FINISHED,
        FAILED
    }

    private SlayerType type;
    private SlayerBoss boss;
    private Player player;
    private int neededExp;
    private int exp;

    private QuestState state;
    private long timeToSpawn;
    private String bossName;
    private long timeToKill;
    private int expReward;

    private static final HashMap<String, Integer> XP_NEEDED = new HashMap<String, Integer>() {{
        put("REVENANT_1", 150);
        put("REVENANT_2", 1440);
        put("REVENANT_3", 2400);
        put("REVENANT_4", 4800);

        put("TARANTULA_1", 150);
        put("TARANTULA_2", 600);
        put("TARANTULA_3", 1000);
        put("TARANTULA_4", 2000);

        put("SVEN_1", 250);
        put("SVEN_2", 600);
        put("SVEN_3", 1500);
        put("SVEN_4", 3000);
    }};

    public SlayerQuest(Player player, SlayerBoss boss){
        exp = 0;
        this.type = boss.getSlayerType();
        this.boss = boss;
        this.state = QuestState.SUMMONING;
        this.player = player;
        this.timeToSpawn = System.currentTimeMillis();

        this.bossName = this.boss.getEntityData().entityName + " " + this.boss.getLevel();
        this.expReward = this.boss.getRewardXp();

        this.neededExp = XP_NEEDED.get(type.name() + "_" + boss.getLevel());
    }

    public void addExp(int exp) {
        setExp(getExp() + exp);
    }

    public EntityType getMobType() {
        switch (type) {
            case REVENANT:
                return EntityType.ZOMBIE;
            case TARANTULA:
                return EntityType.SPIDER;
            case SVEN:
                return EntityType.WOLF;
            default:
                return EntityType.PLAYER;
        }
    }

    public void fail() {
        boss.setFailed(true);
        boss.getEntityData().health = -1;

        setState(QuestState.FAILED);

        Util.delay(() -> {
            player.sendMessage("  " + ChatColor.RED + ChatColor.BOLD + "SLAYER QUEST FAILED!");
            player.sendMessage("   " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "→ " + ChatColor.GRAY +
                    "You died! What a noob!");
        }, 2);
    }
}
