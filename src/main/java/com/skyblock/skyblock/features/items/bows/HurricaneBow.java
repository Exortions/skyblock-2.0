package com.skyblock.skyblock.features.items.bows;

import com.skyblock.skyblock.events.SkyblockEntityDeathEvent;
import com.skyblock.skyblock.features.items.DynamicLore;
import com.skyblock.skyblock.features.items.ListeningItem;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class HurricaneBow extends ListeningItem implements DynamicLore {

    private static final HashMap<String, Integer> upgrades = new HashMap<String, Integer>() {{
        put("Double Shot", 20);
        put("Triple Shot", 50);
        put("Quadruple Shot", 100);
        put("Penta Shot", 250);
    }};

    public HurricaneBow() {
        super(plugin.getItemHandler().getItem("HURRICANE_BOW.json"), "hurricane_bow");
    }

    @Override
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        NBTItem nbtItem = new NBTItem(event.getBow());
        String upgrade = nbtItem.getString("hurricane_bow_upgrade");

        if (upgrade.equals("")) return;

        int additionalArrowsToShoot = this.getArrowsShot(upgrade) - 1;

        for (int i = 0; i < additionalArrowsToShoot; i++) {
            Arrow arrow = event.getEntity().getWorld().spawnArrow(event.getProjectile().getLocation(), event.getProjectile().getVelocity(), event.getForce() * 3.0f, 1);
            arrow.setShooter(event.getEntity());
        }
    }

    @EventHandler
    public void onKill(SkyblockEntityDeathEvent event) {
        ItemStack inHand = event.getKiller().getBukkitPlayer().getItemInHand();

        if (!isThisItem(inHand)) return;
        NBTItem nbtItem = new NBTItem(inHand);
        ItemBase base;

        String upgrade = nbtItem.getString("hurricane_bow_upgrade");
        int kills = nbtItem.getInteger("hurricane_bow_kills");

        if (Objects.equals(upgrade, "")) {
            upgrade = "None";
            kills = 0;

            nbtItem.setString("hurricane_bow_upgrade", upgrade);
            nbtItem.setInteger("hurricane_bow_kills", kills);
        }

        kills++;

        nbtItem.setInteger("hurricane_bow_kills", kills);

        if (kills >= upgrades.get(this.getNextUpgrade(upgrade))) {
            upgrade = this.getNextUpgrade(upgrade);

            nbtItem.setString("hurricane_bow_upgrade", upgrade);
        }

        base = new ItemBase(nbtItem.getItem());

        replaceLore(base);

        event.getKiller().getBukkitPlayer().setItemInHand(base.getStack());
    }

    @Override
    public void onRegenerate(ItemBase item) {
        replaceLore(item);
    }

    @Override
    public String[] toReplace() {
        return new String[]{
                ChatColor.GRAY + "Next Upgrade: ",
                ChatColor.GRAY + "Kills: "
        };
    }

    @Override
    public String[] replaceWith(NBTItem nbtItem) {
        String currentUpgrade = nbtItem.getString("hurricane_bow_upgrade");

        if (currentUpgrade == null) {
            currentUpgrade = "Double Shot";
            nbtItem.setString("hurricane_bow_upgrade", currentUpgrade);
        }

        int kills;

        try {
            kills = nbtItem.getInteger("hurricane_bow_kills");
        } catch (Exception ex) {
            kills = 0;

            nbtItem.setInteger("hurricane_bow_kills", kills);
        }

        String nextUpgrade = getNextUpgrade(currentUpgrade);
        int nextUpgradeKills = 0;

        if (upgrades.get(nextUpgrade) == null) nextUpgrade = "Maxed";
        else nextUpgradeKills = upgrades.get(nextUpgrade);

        return new String[]{
                nextUpgrade.equals("Maxed") ? ChatColor.GRAY + "Next Upgrade: " + ChatColor.GREEN + "" + ChatColor.BOLD + "MAXED!" : ChatColor.GRAY + "Next Upgrade: " + ChatColor.YELLOW + nextUpgrade + ChatColor.DARK_GRAY + " (" + ChatColor.GREEN + kills + ChatColor.GRAY + "/" + ChatColor.RED + nextUpgradeKills + ChatColor.DARK_GRAY + ")",
                ChatColor.GRAY + "Kills: " + ChatColor.AQUA + kills
        };
    }

    public String getNextUpgrade(String currentUpgrade) {
        switch (currentUpgrade) {
            case "Double Shot":
                return "Triple Shot";
            case "Triple Shot":
                return "Quadruple Shot";
            case "Quadruple Shot":
                return "Penta Shot";
            case "Penta Shot":
                return "Maxed";
            default:
                return "Double Shot";
        }
    }


    public int getArrowsShot(String upgrade) {
        switch (upgrade) {
            case "Double Shot":
                return 2;
            case "Triple Shot":
                return 3;
            case "Quadruple Shot":
                return 4;
            case "Penta Shot":
                return 5;
            default:
                return 1;
        }
    }

}
